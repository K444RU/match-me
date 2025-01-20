package com.matchme.srv.service;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventType;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserMessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    UserMessageRepository userMessageRepository;

    @InjectMocks
    private ChatService chatService;

    public ChatServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChatPreviews() {
        Long userId = 1L;

        User user1 = new User();
        user1.setId(userId);
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        UserProfile user2Profile = new UserProfile();
        user2Profile.setAlias("TestNickName123321");
        user2.setProfile(user2Profile);

        MessageEventType readEventType = new MessageEventType();
        readEventType.setId(1L);
        readEventType.setName("READ");

        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setMessageEventType(readEventType);

        UserMessage lastMessage = new UserMessage();
        lastMessage.setContent("Hello!");
        lastMessage.setCreatedAt(Instant.now());
        lastMessage.setUser(user2);
        lastMessage.setMessageEvents(Set.of(messageEvent));

        Connection connection = new Connection();
        connection.setId(101L);
        connection.getUsers().add(user1);
        connection.getUsers().add(user2);
        connection.setUserMessages(Set.of(lastMessage));

        when(connectionRepository.findConnectionsByUserIdWithMessages(userId)).thenReturn(List.of(connection));

        List<ChatPreviewResponseDTO> chatPreviews = chatService.getChatPreviews(userId);

        assertThat(chatPreviews).hasSize(1);
        ChatPreviewResponseDTO preview = chatPreviews.get(0);
        assertThat(preview.getConnectedUserAlias()).isEqualTo("TestNickName123321");
        assertThat(preview.getLastMessageContent()).isEqualTo("Hello!");
        assertThat(preview.getUnreadMessageCount()).isEqualTo(0);
    }

    @Test
    void testGetChatMessagesService() {
        Long connectionId = 101L;
        Long userId = 1L;

        User user1 = new User();
        user1.setId(userId);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");

        UserProfile user2Profile = new UserProfile();
        user2Profile.setAlias("TestNickName123321");
        user2.setProfile(user2Profile);

        UserMessage userMessage = new UserMessage();
        userMessage.setId(1L);
        userMessage.setUser(user2);
        userMessage.setContent("Hello!");
        userMessage.setCreatedAt(Instant.now());

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.getUsers().add(user1);
        connection.getUsers().add(user2);

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

        when(userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(userMessage)));

        Page<ChatMessageResponseDTO> result = chatService.getChatMessages(connectionId, userId, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        ChatMessageResponseDTO messageDTO = result.getContent().get(0);
        assertThat(messageDTO.getSenderAlias()).isEqualTo("TestNickName123321");
        assertThat(messageDTO.getContent()).isEqualTo("Hello!");
    }

    @Test
    void testGetChatMessagesUserNotInConnection() {
        Long connectionId = 101L;
        Long userId = 1L;

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setUsers(Set.of(new User()));

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

        assertThrows(RuntimeException.class, () -> {
            chatService.getChatMessages(connectionId, userId, PageRequest.of(0, 10));
        });
    }

    @Test
    void testGetChatMessagesNoMessages() {
        Long connectionId = 101L;
        Long userId = 1L;

        User user1 = new User();
        user1.setId(userId);

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setUsers(Set.of(user1));

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
        when(userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<ChatMessageResponseDTO> result = chatService.getChatMessages(connectionId, userId, PageRequest.of(0, 10));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void testSaveMessage() {
        Long connectionId = 101L;
        Long senderId = 1L;
        String content = "Test message content";
        Instant timestamp = Instant.now();

        User sender = new User();
        sender.setId(senderId);
        sender.setProfile(UserProfile.builder().alias("TestNickName123321").build());

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setUsers(Set.of(sender));

        UserMessage userMessage = new UserMessage();
        userMessage.setId(1L);
        userMessage.setContent(content);
        userMessage.setCreatedAt(timestamp);
        userMessage.setUser(sender);
        userMessage.setConnection(connection);
        userMessage.setMessageEvents(new HashSet<>());

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));
        when(userMessageRepository.save(any(UserMessage.class))).thenReturn(userMessage);

        ChatMessageResponseDTO response = chatService.saveMessage(connectionId, senderId, content, timestamp);

        assertThat(response.getMessageId()).isEqualTo(1L);
        assertThat(response.getConnectionId()).isEqualTo(101L);
        assertThat(response.getSenderAlias()).isEqualTo("TestNickName123321");
        assertThat(response.getContent()).isEqualTo("Test message content");
        assertThat(response.getCreatedAt()).isEqualTo(timestamp);
    }

    @Test
    void testGetOtherUserIdInConnection() {
        Long connectionId = 101L;
        Long senderId = 1L;
        Long otherUserId = 2L;

        User sender = new User();
        sender.setId(senderId);

        User otherUser = new User();
        otherUser.setId(otherUserId);

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setUsers(Set.of(sender, otherUser));

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

        Long result = chatService.getOtherUserIdInConnection(connectionId, senderId);

        assertThat(result).isEqualTo(otherUserId);
    }

    @Test
    void testGetOtherUserIdInConnectionUserNotFound() {
        Long connectionId = 101L;
        Long senderId = 1L;

        User sender = new User();
        sender.setId(senderId);

        Connection connection = new Connection();
        connection.setId(connectionId);
        connection.setUsers(Set.of(sender));

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.of(connection));

        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getOtherUserIdInConnection(connectionId, senderId);
        });
    }

    @Test
    void testGetOtherUserIdInConnectionConnectionNotFound() {
        Long connectionId = 101L;
        Long senderId = 1L;

        when(connectionRepository.findById(connectionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            chatService.getOtherUserIdInConnection(connectionId, senderId);
        });
    }
}
