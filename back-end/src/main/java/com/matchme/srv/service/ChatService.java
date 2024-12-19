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

    /**
     * This method fetches a list of chat previews for a given user.
     * Each chat preview includes the most recent chat details the last message,
     * connected user's alias, the last message's date, and the count of unread messages.
     * 1. **Fetch User's Connections**:
     * - We call the repository method `findConnectionsByUserId(userId)` to get all
     * connections (chats) involving the user.
     * - A "connection" here represents a chat session between the current user and
     * another user.
     * 2. **Find the Other Participant**:
     * - We use `Optional<User> optionalOtherParticipant = connection.getUsers().stream()`
     * to find the **other user** in the connection.
     * - We filter out the current user (`userId`) to get the connected user's details.
     * - Using `Optional` is important because it prevents `null` errors if no other
     * user is found in the connection.
     * - `Optional<User>` ensures that we safely handle situations where there might not be
     * a valid "other user" in the connection.
     * - It avoids potential `NullPointerException` errors by using methods like
     * `isEmpty()` and `get()` for safe access.
     * 3. **Connected User Alias**:
     * - Once we find the other participant, we retrieve their alias using
     * `otherParticipant.getProfile().getAlias()`.
     * - The alias is the connected user's display name in the chat.
     * 4. **Last Sent Message**:
     * - From the set of messages in the connection, we find the **most recent message**
     * using:
     * `messages.stream().max(Comparator.comparing(UserMessage::getCreatedAt))`.
     * - This ensures we get the last message based on its creation timestamp.
     * - The content (`lastMessage.getContent()`) and timestamp
     * (`lastMessage.getCreatedAt()`) are set in the preview.
     * 5. **Unread Message Count**:
     * - We calculate the unread messages by filtering messages sent by the **other user**:
     * - Check if the message does NOT have a "READ" event in its `messageEvents`.
     * - Use `stream().noneMatch()` to see if the "READ" event exists.
     * - Count the messages that match the unread condition.
     * 6. **Sorting Chats**:
     * https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-
     * - After creating all the chat previews, we sort them by the **last message's timestamp**.
     * - The most recent chats appear first (descending order).
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
                            .noneMatch(messageEvent -> messageEvent.getMessageEventType().getName().equals("READ")))
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
        deliveredEvent.setMessageEventType(new MessageEventType(1L, "SENT"));
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
