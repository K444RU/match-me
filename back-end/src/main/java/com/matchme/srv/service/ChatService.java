package com.matchme.srv.service;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventType;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserMessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConnectionRepository connectionRepository;
    private final UserMessageRepository userMessageRepository;

    public static final String EVENT_TYPE_READ = "READ";
    public static final String EVENT_TYPE_SEND = "SEND";

    /**
     * Fetches a list of chat previews for the given user.
     * - Retrieves all chat connections for the user.
     * - For each connection, determines the other participant and fetches details
     * like their alias and the last message in the chat.
     * - Counts unread messages for the user in each chat.
     * - Sorts the chats by the timestamp of the last message, with the newest first.
     * - https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-
     * - Returns a list of chat previews, including the connection ID, alias,
     * last message, and unread count.
     */
    public List<ChatPreviewResponseDTO> getChatPreviews(Long userId) {

        List<Connection> connections = connectionRepository.findConnectionsByUserId(userId);
        List<ChatPreviewResponseDTO> chatPreviews = new ArrayList<>();

        for (Connection connection : connections) {
            ChatPreviewResponseDTO chatPreview = new ChatPreviewResponseDTO();
            chatPreview.setConnectionId(connection.getId());

            Optional<User> optionalOtherParticipant = connection.getUsers().stream()
                    .filter(user -> !user.getId().equals(userId))
                    .findFirst();

            if (optionalOtherParticipant.isEmpty()) {
                continue;
            }

            User otherParticipant = optionalOtherParticipant.get();
            chatPreview.setConnectedUserId(otherParticipant.getId());
            chatPreview.setConnectedUserAlias(otherParticipant.getProfile().getAlias());

            Set<UserMessage> messages = connection.getUserMessages();
            if (!messages.isEmpty()) {
                Optional<UserMessage> optionalLastMessage = messages.stream()
                        .max(Comparator.comparing(UserMessage::getCreatedAt));

                if (optionalLastMessage.isPresent()) {
                    UserMessage lastMessage = optionalLastMessage.get();
                    chatPreview.setLastMessageContent(lastMessage.getContent());
                    chatPreview.setLastMessageTimestamp(lastMessage.getCreatedAt());
                }
            }

            int unreadMessageCount = (int) messages.stream()
                    .filter(userMessage -> !userMessage.getUser().getId().equals(userId))
                    .filter(userMessage -> userMessage.getMessageEvents().stream()
                            .noneMatch(messageEvent -> EVENT_TYPE_READ.equals(messageEvent.getMessageEventType().getName())))
                    .count();

            chatPreview.setUnreadMessageCount(unreadMessageCount);
            chatPreviews.add(chatPreview);
        }

        chatPreviews.sort(
                Comparator.comparing(
                        ChatPreviewResponseDTO::getLastMessageTimestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        return chatPreviews;
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
            User sender = message.getUser();
            return new ChatMessageResponseDTO(
                    connection.getId(),
                    message.getId(),
                    sender.getProfile().getAlias(),
                    message.getContent(),
                    message.getCreatedAt()
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
    * Saves a newly created message to user_messages table
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
    public ChatMessageResponseDTO saveMessage(Long connectionId, Long senderId, String content, Timestamp timestamp) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        boolean isParticipant = connection.getUsers().stream()
                .anyMatch(u -> u.getId().equals(senderId));
        if (!isParticipant) {
            throw new RuntimeException("User is not participant.");
        }

        UserMessage message = new UserMessage();
        message.setConnection(connection);
        message.setUser(connection.getUsers().stream()
                .filter(user -> user.getId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found")));
        message.setContent(content);
        message.setCreatedAt(timestamp);

        UserMessage savedMessage = userMessageRepository.save(message);

        MessageEvent deliveredEvent = new MessageEvent();
        deliveredEvent.setMessage(savedMessage);
        //toDo: Refactor using constants for "SENT" to avoid hardcoding.
        deliveredEvent.setMessageEventType(new MessageEventType(1L, EVENT_TYPE_SEND));
        deliveredEvent.setTimestamp(timestamp);
        savedMessage.getMessageEvents().add(deliveredEvent);

        userMessageRepository.save(savedMessage);

        return new ChatMessageResponseDTO(
                savedMessage.getId(),
                connectionId,
                savedMessage.getUser().getProfile().getAlias(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt()
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
}
