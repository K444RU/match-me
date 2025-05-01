package com.matchme.srv.service;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.dto.response.MessageEventDTO;
import com.matchme.srv.dto.response.MessageStatusUpdateDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventTypeEnum;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.publisher.ChatPublisher;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.MessageEventRepository;
import com.matchme.srv.repository.UserMessageRepository;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConnectionRepository connectionRepository;
    private final UserMessageRepository userMessageRepository;
    private final MessageEventRepository messageEventRepository;
    private final ConnectionService connectionService;
    private final ChatPublisher chatPublisher;

    /**
     * Retrieves chat previews for the specified user.
     *<p>
     * The method gathers an overview of all chat conversations for a user, including:
     * - The connection ID and details of the other participant (alias, name, profile picture).
     * - The last message exchanged in each chat along with its timestamp.
     * - The count of unread messages per chat.
     *<p>
     * Process Overview:
     * 1. Fetch all chat connections for the user.
     * 2. For each connection:
     *    - Identify the other participant.
     *    - Retrieve the participant's profile details.
     *    - Find the most recent message.
     *    - Count unread messages where no "READ" event exists.
     * 3. Sort the previews by the timestamp of the last message, with the newest first.
     * 4. Return a list of `ChatPreviewResponseDTO` objects containing the compiled details.
     *<p>
     * @param userId The ID of the user for whom to fetch chat previews.
     * @return A sorted list of chat previews for ACCEPTED connections.
     */
    @Transactional
    public List<ChatPreviewResponseDTO> getChatPreviews(Long userId) {
        // 1) Fetch connections (without fetching messages)
        List<Connection> connections = connectionRepository.findConnectionsByUserId(userId);
        List<ChatPreviewResponseDTO> chatPreviews = new ArrayList<>();

        // 2) Iterate over each connection
        for (Connection connection : connections) {
            ConnectionState latestState = connection.getConnectionStates().stream()
                    .max(Comparator.comparing(ConnectionState::getTimestamp))
                    .orElse(null); // Find the state with the most recent timestamp

            // ONLY include the connection if its very latest status is ACCEPTED
            if (latestState == null || latestState.getStatus() != ConnectionStatus.ACCEPTED) {
                // If there's no state or the latest isn't ACCEPTED (could be PENDING, REJECTED, DISCONNECTED)
                // then skip this connection for chat preview purposes.
                continue;
            }

            User otherParticipant = findOtherParticipant(connection, userId);
            if (otherParticipant == null) {
                continue;
            }

            // Build the preview object
            ChatPreviewResponseDTO preview = new ChatPreviewResponseDTO();
            preview.setConnectionId(connection.getId());
            preview.setConnectedUserId(otherParticipant.getId());

            // Fill participant info (alias, name, picture)
            fillParticipantDetails(preview, otherParticipant);

            // Last message
            UserMessage lastMessage = userMessageRepository
                    .findTopByConnectionIdOrderByCreatedAtDesc(connection.getId());
            if (lastMessage != null) {
                preview.setLastMessageContent(lastMessage.getContent());
                preview.setLastMessageTimestamp(lastMessage.getCreatedAt());
            } else {
                preview.setLastMessageContent(null);
                preview.setLastMessageTimestamp(null);
            }

            // Unread count (DB-level query)
            int unreadCount = userMessageRepository.countUnreadMessages(connection.getId(), userId, MessageEventTypeEnum.READ);
            preview.setUnreadMessageCount(unreadCount);

            chatPreviews.add(preview);
        }

        // 3) Sort by lastMessageTimestamp descending (nulls last)
        chatPreviews.sort(Comparator.comparing(
                ChatPreviewResponseDTO::getLastMessageTimestamp,
                Comparator.nullsLast(Comparator.reverseOrder())
        ));

        return chatPreviews;
    }

    private User findOtherParticipant(Connection connection, Long currentUserId) {
        return connection.getUsers().stream()
                .filter(u -> !u.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);
    }

    private void fillParticipantDetails(ChatPreviewResponseDTO preview, User participant) {
        if (participant.getProfile() == null) {
            preview.setConnectedUserAlias("UNKNOWN_ALIAS");
            return;
        }

        String alias = participant.getProfile().getAlias();
        preview.setConnectedUserAlias(alias != null ? alias : "UNKNOWN_ALIAS");

        preview.setConnectedUserFirstName(participant.getProfile().getFirst_name());
        preview.setConnectedUserLastName(participant.getProfile().getLast_name());

        byte[] picBytes = participant.getProfile().getProfilePicture();
        String dataUrl = encodeImageToDataUrl(picBytes, "image/png");
        if (dataUrl != null) {
            preview.setConnectedUserProfilePicture(dataUrl);
        } else {
          preview.setConnectedUserProfilePicture("");
        }
    }

    /**
     * Converts raw image bytes into a Data URL (data:[imageMimeType];base64,...)
     * that can be directly used in <img> tags, etc.
     *<p>
     * Returns null if the byte array is null or empty.
     * This helper method can be extended to handle multiple image formats or validations.
     *
     * @param imageBytes the raw bytes of the image
     * @param imageMimeType the MIME type, e.g. "image/png", "image/jpeg"
     * @return a data URL string, or null if imageBytes are empty
     */
    private String encodeImageToDataUrl(byte[] imageBytes, String imageMimeType) {
        if (imageBytes == null || imageBytes.length == 0) {
            return null;
        }
        return "data:" + imageMimeType + ";base64,"
                + Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Fetches paginated messages for a specific connection and user.
     * - Validates that the connection exists and the user is a participant in it.
     * - Retrieves all messages for the connection, ordered by the time they were created.
     * - Maps each message to a `ChatMessageResponseDTO`, which includes sender details,
     * content, and timestamp.
     * - Returns a paginated list of chat messages.
     */
    public Page<ChatMessageResponseDTO> getChatMessages(Long connectionId, Long userId, Pageable pageable) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        boolean isParticipant = connection.getUsers().stream().anyMatch(u -> u.getId().equals(userId));
        if (!isParticipant) {
            throw new RuntimeException("User is not participant.");
        }

        Page<UserMessage> messages = userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(connectionId, pageable);

        return messages.map(message -> {
            User sender = message.getSender();
            MessageEventDTO messageEvent = getLatestMessageEventDTO(message);
            
            return new ChatMessageResponseDTO(
                    message.getId(),
                    connectionId,
                    sender.getId(),
                    sender.getProfile().getAlias(),
                    message.getContent(),
                    message.getCreatedAt(),
                    messageEvent
            );
        });
    }

    /**
    * Saves a new chat message and creates a "SENT" event.
    * - Validates that the connection exists and the sender is a participant in it.
    * - Creates a new `UserMessage` entity, associates it with the connection and sender,
    * and sets its content and timestamp.
    * - Persists the message to the database.
    * - Creates a "SENT" event for the message and associates it with the message.
    * - Saves the message again with the event included.
    * - Returns a `ChatMessageResponseDTO` with the message details.
    * -@Transactional annotation ensures database consistency
    *
    * @param  connectionId - Connection to send to
    * @param  senderId
    * @param  content
    * @param  timestamp - Message creation/sent at timestamp
    * @return ChatMessageResponseDTO
    * @see  ChatMessageResponseDTO
    * @see  Connection
    * @see  UserMessage
    * @see  MessageEvent
    */
    @Transactional
    public ChatMessageResponseDTO saveMessage(Long connectionId, Long senderId, String content, Instant timestamp, boolean isOtherUserOnline) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        boolean isParticipant = connection.getUsers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isParticipant) {
            throw new RuntimeException("User is not participant.");
        }

        UserMessage message = UserMessage.builder()
                .connection(connection)
                .sender(connection.getUsers().stream()
                        .filter(user -> user.getId().equals(senderId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("User not found")))
                .content(content)
                .createdAt(timestamp)
                .messageEvents(new HashSet<>())
                .build();

        // Create and add the SENT event
        MessageEvent sentEvent = new MessageEvent();
        sentEvent.setMessage(message);
        sentEvent.setMessageEventType(MessageEventTypeEnum.SENT);
        sentEvent.setTimestamp(timestamp);
        message.getMessageEvents().add(sentEvent);

        Instant receivedTimestamp = null;

        // If recipient is online, create and add the RECEIVED event immediately
        if (isOtherUserOnline) {
            MessageEvent receivedEvent = new MessageEvent();
            receivedEvent.setMessage(message);
            receivedEvent.setMessageEventType(MessageEventTypeEnum.RECEIVED);
            // Use the same timestamp as SENT for simplicity, or Instant.now() if preferred
            receivedEvent.setTimestamp(timestamp);
            message.getMessageEvents().add(receivedEvent);
            receivedTimestamp = receivedEvent.getTimestamp();
        }

        // Save the message along with its events
        UserMessage savedMessage = userMessageRepository.save(message);

        // Get the latest event DTO (will be RECEIVED if recipient was online, otherwise SENT)
        MessageEventDTO messageEvent = getLatestMessageEventDTO(savedMessage);

        if (receivedTimestamp != null) {
            MessageStatusUpdateDTO statusUpdate = new MessageStatusUpdateDTO(
                    savedMessage.getId(), connectionId, MessageEventTypeEnum.RECEIVED, receivedTimestamp);
            chatPublisher.publishStatusUpdate(senderId, statusUpdate);
            log.debug(
                "Published RECEIVED status update via ChatPublisher for msg {} to sender {}",
                savedMessage.getId(),
                senderId);
        }

        return new ChatMessageResponseDTO(
                savedMessage.getId(),
                connectionId,
                senderId,
                savedMessage.getSender().getProfile().getAlias(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt(),
                messageEvent
        );
    }

    /**
     * Finds the other participant's user ID in a chat connection.
     * - Validates that the connection exists.
     * - Identifies the user in the connection who is not the sender.
     * - Returns the other user's ID, or throws an exception if not found.
     */
    @Transactional
    public Long getOtherUserIdInConnection(Long connectionId, Long senderId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        return connection.getUsers().stream()
                .filter(user -> !user.getId().equals(senderId))
                .map(User::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    /**
     * Retrieves all connection IDs for a user.
     *
     * @param userId The ID of the user whose connections we want to fetch.
     * @return A list of connection IDs.
     */
    public List<Long> getUserConnections(Long userId) {
        return connectionRepository.findConnectionsByUserId(userId).stream()
                .map(Connection::getId).toList();
    }

    @Transactional
    public ChatPreviewResponseDTO markMessagesAsRead(Long connectionId, Long userId) {
        Connection connection =
            connectionRepository
                .findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        boolean isParticipant = connection.getUsers().stream().anyMatch(u -> u.getId().equals(userId));
        if (!isParticipant) {
        throw new RuntimeException("User is not participant.");
        }

        List<UserMessage> messagesToMarkRead =
            userMessageRepository.findMessagesToMarkAsRead(connectionId, userId, MessageEventTypeEnum.READ);

        User otherParticipant = findOtherParticipant(connection, userId);

        if (!messagesToMarkRead.isEmpty()) {
        Instant now = Instant.now();
        List<MessageEvent> readEvents = new ArrayList<>();
        List<MessageStatusUpdateDTO> statusUpdates = new ArrayList<>();

        for (UserMessage message : messagesToMarkRead) {
            if (message.getSender().getId().equals(userId)) {
                log.warn("Attempted to mark message {} as read, but we were the sender {}. Skipping.", message.getId(), userId);
                continue;
            }

            MessageEvent readEvent = new MessageEvent();
            readEvent.setMessage(message);
            readEvent.setMessageEventType(MessageEventTypeEnum.READ);
            readEvent.setTimestamp(now);
            readEvents.add(readEvent);

            MessageStatusUpdateDTO statusUpdate = new MessageStatusUpdateDTO(
                message.getId(),
                connectionId,
                MessageEventTypeEnum.READ,
                now
            );
            statusUpdates.add(statusUpdate);
            log.trace("Queued READ status update for msg {} to sender {}", message.getId(), message.getSender().getId());
        }
        messageEventRepository.saveAll(readEvents);
        log.info("Saved {} READ events for reader {} in connection {}", readEvents.size(), userId, connectionId);

        if (otherParticipant == null) {
            // Handle this error appropriately, maybe log and don't send notifications
             log.error("Cannot find sender to notify for read status in connection {}. Reader: {}", connectionId, userId);
        } else {
            Long senderId = otherParticipant.getId(); // The user who needs the notification

            if (!statusUpdates.isEmpty()) {
                log.info("Publishing {} READ status updates via ChatPublisher to sender {}", statusUpdates.size(), senderId);
                for (MessageStatusUpdateDTO updateDTO : statusUpdates) {
                    chatPublisher.publishStatusUpdate(senderId, updateDTO);
                    log.trace("Published READ status update for msg {}", updateDTO.getMessageId());
                }
            }
        }
        }

        ChatPreviewResponseDTO preview = new ChatPreviewResponseDTO();
        preview.setConnectionId(connectionId);

        if (otherParticipant != null) { // Check again in case of error above
            preview.setConnectedUserId(otherParticipant.getId());
            fillParticipantDetails(preview, otherParticipant);
       } else {
            preview.setConnectedUserId(null);
            preview.setConnectedUserAlias("UNKNOWN");
       }

        UserMessage lastMessage =
            userMessageRepository.findTopByConnectionIdOrderByCreatedAtDesc(connection.getId());
        if (lastMessage != null) {
            preview.setLastMessageContent(lastMessage.getContent());
            preview.setLastMessageTimestamp(lastMessage.getCreatedAt());
        } else {
            preview.setLastMessageContent(null);
            preview.setLastMessageTimestamp(null);
        }

        preview.setUnreadMessageCount(0);

        return preview;
    }

    @Transactional
    public void markAllMessagesAsReceived(Long userId) {
        log.info("Checking messages to mark as RECEIVED for newly online user ID: {}", userId);
        List<Connection> connections = connectionRepository.findConnectionsByUserId(userId);
        List<MessageEvent> newReceivedEvents = new ArrayList<>();
        Map<Long, List<MessageStatusUpdateDTO>> updatesToSend = new HashMap<>();
        Instant now = Instant.now();

        for (Connection connection : connections) {
        ConnectionState currentState = connectionService.getCurrentState(connection);
        if (currentState == null || currentState.getStatus() != ConnectionStatus.ACCEPTED) {
            continue;
        }

        Long connectionId = connection.getId();
        User otherParticipant = findOtherParticipant(connection, userId);
        if (otherParticipant == null) {
            log.warn(
                "Could not find other participant for connection ID {} while marking messages received"
                    + " for user ID {}",
                connectionId,
                userId);
            continue;
        }
        Long otherUserId = otherParticipant.getId();

        // Find messages sent BY the other user (TO the current user) in this connection
        // Use the query that fetches events eagerly
        List<UserMessage> messagesSentByOther =
            userMessageRepository.findByConnectionIdAndSenderIdFetchEvents(connectionId, otherUserId);

            for (UserMessage message : messagesSentByOther) {
                Optional<MessageEvent> latestEventOpt = message.getMessageEvents().stream()
                        .max(Comparator.comparing(MessageEvent::getTimestamp));

                if (latestEventOpt.isPresent() && latestEventOpt.get().getMessageEventType() == MessageEventTypeEnum.SENT) {
                    // Add RECEIVED event
                    MessageEvent receivedEvent = new MessageEvent();
                    receivedEvent.setMessage(message);
                    receivedEvent.setMessageEventType(MessageEventTypeEnum.RECEIVED);
                    receivedEvent.setTimestamp(now);
                    newReceivedEvents.add(receivedEvent);

                    // Prepare status update DTO for the original sender 
                    MessageStatusUpdateDTO statusUpdate = new MessageStatusUpdateDTO(
                        message.getId(),
                        connection.getId(),
                        MessageEventTypeEnum.RECEIVED,
                        now
                    );
                    // Group updates by the sender
                    updatesToSend.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(statusUpdate);

                    log.trace("Queued RECEIVED status update for msg {} to sender {}", message.getId(), otherUserId);
                }
            }
        }

        if (!newReceivedEvents.isEmpty()) {
          messageEventRepository.saveAll(newReceivedEvents);
          log.info("Marked {} messages as RECEIVED for user ID: {}", newReceivedEvents.size(), userId);

          if (!updatesToSend.isEmpty()) {
            log.info("Publishing RECEIVED status updates via ChatPublisher for {} senders", updatesToSend.size());
            updatesToSend.forEach(
                (senderId, statusUpdateList) -> {
                  log.debug(
                      "Publishing {} RECEIVED updates to sender {}", statusUpdateList.size(), senderId);
                  for (MessageStatusUpdateDTO updateDTO : statusUpdateList) {
                    chatPublisher.publishStatusUpdate(senderId, updateDTO);
                    log.trace("Published RECEIVED status update for msg {}", updateDTO.getMessageId());
                  }
                });
          }

        } else {
        log.info("No messages needed marking as RECEIVED for user ID: {}", userId);
        }
    }

    private MessageEventDTO getLatestMessageEventDTO(UserMessage message) {
        if (message.getMessageEvents() == null || message.getMessageEvents().isEmpty()) {
            return null;
        }

        MessageEvent latestMessageEvent = message.getMessageEvents().stream()
                .max(Comparator.comparing(MessageEvent::getTimestamp))
                .orElseThrow(() -> new RuntimeException("No message events found"));

        return MessageEventDTO.builder()
                .type(latestMessageEvent.getMessageEventType())
                .timestamp(latestMessageEvent.getTimestamp())
                .build();
    }
}
