package com.matchme.srv.service;

import static com.matchme.srv.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventType;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.repository.ConnectionRepository;
import com.matchme.srv.repository.UserMessageRepository;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock private ConnectionRepository connectionRepository;
  @Mock private UserMessageRepository userMessageRepository;
  @InjectMocks private ChatService chatService;

  private static final Long CONNECTION_ID = 101L;
  private static final String MESSAGE_CONTENT = "Hello!";

  private User user;
  private User otherUser;
  private UserMessage message;
  private Connection connection;
  private MessageEventType readEventType;
  private MessageEvent messageEvent;

  @BeforeEach
  void setUp() {
    user = createUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    otherUser = createUser(DEFAULT_TARGET_USER_ID, DEFAULT_TARGET_EMAIL);
    otherUser.setProfile(createBasicProfile());

    readEventType = new MessageEventType();
    readEventType.setId(1L);
    readEventType.setName("READ");

    messageEvent = new MessageEvent();
    messageEvent.setMessageEventType(readEventType);

    message =
        UserMessage.builder()
            .id(1L)
            .content(MESSAGE_CONTENT)
            .createdAt(Instant.now())
            .user(otherUser)
            .messageEvents(new HashSet<>(Set.of(messageEvent)))
            .build();

    connection = Connection.builder().id(CONNECTION_ID).users(Set.of(user, otherUser)).build();
  }

  @Nested
  @DisplayName("getChatPreviews Tests")
  class GetChatPreviewsTests {

    @Test
    @DisplayName("Should return chat previews when connections exist")
    void getChatPreviews_WithConnections_ReturnsPreviews() {
      // Arrange
      when(connectionRepository.findConnectionsByUserId(DEFAULT_USER_ID))
          .thenReturn(List.of(connection));
      when(userMessageRepository.findTopByConnectionIdOrderByCreatedAtDesc(CONNECTION_ID))
          .thenReturn(message);
      when(userMessageRepository.countUnreadMessages(CONNECTION_ID, DEFAULT_USER_ID)).thenReturn(0);

      // Act
      List<ChatPreviewResponseDTO> chatPreviews = chatService.getChatPreviews(DEFAULT_USER_ID);

      // Assert
      assertAll(
          () -> assertThat(chatPreviews).hasSize(1),
          () -> {
            ChatPreviewResponseDTO preview = chatPreviews.get(0);
            assertThat(preview.getConnectedUserAlias())
                .as("checking if the alias is correct")
                .isEqualTo(DEFAULT_ALIAS);
            assertThat(preview.getLastMessageContent())
                .as("checking if the message content is correct")
                .isEqualTo(MESSAGE_CONTENT);
            assertThat(preview.getUnreadMessageCount())
                .as("checking if the unread count is correct")
                .isZero();
          },
          () -> verify(connectionRepository, times(1)).findConnectionsByUserId(DEFAULT_USER_ID));
    }
  }

  @Nested
  @DisplayName("getChatMessages Tests")
  class GetChatMessagesTests {

    @Test
    @DisplayName("Should return chat messages when connection exists")
    void getChatMessages_ValidConnection_ReturnsMessages() {
      // Arrange
      PageRequest pageRequest = PageRequest.of(0, 10);
      when(connectionRepository.findById(CONNECTION_ID)).thenReturn(Optional.of(connection));
      when(userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(
              anyLong(), any(PageRequest.class)))
          .thenReturn(new PageImpl<>(List.of(message)));

      // Act
      Page<ChatMessageResponseDTO> result =
          chatService.getChatMessages(CONNECTION_ID, DEFAULT_USER_ID, pageRequest);

      // Assert
      assertAll(
          () -> assertThat(result.getContent()).hasSize(1),
          () -> {
            ChatMessageResponseDTO messageDTO = result.getContent().get(0);
            assertThat(messageDTO.getSenderAlias())
                .as("checking if the sender alias is correct")
                .isEqualTo(DEFAULT_ALIAS);
            assertThat(messageDTO.getContent())
                .as("checking if the message content is correct")
                .isEqualTo(MESSAGE_CONTENT);
          },
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID),
          () ->
              verify(userMessageRepository, times(1))
                  .findByConnectionIdOrderByCreatedAtDesc(anyLong(), any(PageRequest.class)));
    }

    @Test
    @DisplayName("Should throw exception when user not in connection")
    void getChatMessages_UserNotInConnection_ThrowsException() {
      // Arrange
      User differentUser = createUser(DEFAULT_TARGET_USER_ID, DEFAULT_TARGET_EMAIL);
      Connection mockConnection =
          Connection.builder().id(CONNECTION_ID).users(Set.of(differentUser)).build();

      when(connectionRepository.findById(CONNECTION_ID)).thenReturn(Optional.of(mockConnection));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () ->
                          chatService.getChatMessages(
                              CONNECTION_ID, DEFAULT_USER_ID, PageRequest.of(0, 10)))
                  .as("checking if exception is thrown when user not in connection")
                  .isInstanceOf(RuntimeException.class)
                  .hasMessageContaining("User is not participant"),
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID));
    }
  }

  @Nested
  @DisplayName("saveMessage Tests")
  class SaveMessageTests {

    @Test
    @DisplayName("Should save message when connection exists")
    void saveMessage_ValidConnection_SavesMessage() {
      // Arrange
      Instant timestamp = Instant.now();
      when(connectionRepository.findById(CONNECTION_ID)).thenReturn(Optional.of(connection));

      UserMessage testMessage =
          UserMessage.builder()
              .id(1L)
              .content(MESSAGE_CONTENT)
              .createdAt(timestamp)
              .user(user)
              .messageEvents(new HashSet<>())
              .build();

      when(userMessageRepository.save(any(UserMessage.class))).thenReturn(testMessage);

      // Act
      ChatMessageResponseDTO response =
          chatService.saveMessage(CONNECTION_ID, DEFAULT_USER_ID, MESSAGE_CONTENT, timestamp);

      // Assert
      assertAll(
          () -> assertThat(response.getMessageId()).isEqualTo(1L),
          () -> assertThat(response.getConnectionId()).isEqualTo(CONNECTION_ID),
          () -> assertThat(response.getSenderAlias()).isEqualTo(DEFAULT_ALIAS),
          () -> assertThat(response.getContent()).isEqualTo(MESSAGE_CONTENT),
          () -> assertThat(response.getCreatedAt()).isEqualTo(timestamp),
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID),
          () -> verify(userMessageRepository, times(1)).save(any(UserMessage.class)));
    }
  }

  @Nested
  @DisplayName("getOtherUserIdInConnection Tests")
  class GetOtherUserIdInConnectionTests {

    @Test
    @DisplayName("Should return other user ID when connection exists")
    void getOtherUserId_ValidConnection_ReturnsId() {
      // Arrange
      when(connectionRepository.findById(CONNECTION_ID)).thenReturn(Optional.of(connection));

      // Act
      Long result = chatService.getOtherUserIdInConnection(CONNECTION_ID, DEFAULT_USER_ID);

      // Assert
      assertAll(
          () -> assertThat(result).isEqualTo(DEFAULT_TARGET_USER_ID),
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID));
    }

    @Test
    @DisplayName("Should throw exception when connection not found")
    void getOtherUserId_ConnectionNotFound_ThrowsException() {
      // Arrange
      when(connectionRepository.findById(CONNECTION_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> chatService.getOtherUserIdInConnection(CONNECTION_ID, DEFAULT_USER_ID))
                  .as("checking if exception is thrown when connection not found")
                  .isInstanceOf(IllegalArgumentException.class),
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID));
    }

    @Test
    @DisplayName("Should throw exception when other user not found in connection")
    void getOtherUserId_OtherUserNotFound_ThrowsException() {
      // Arrange
      Connection singleUserConnection =
          Connection.builder().id(CONNECTION_ID).users(Set.of(user)).build();

      when(connectionRepository.findById(CONNECTION_ID))
          .thenReturn(Optional.of(singleUserConnection));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> chatService.getOtherUserIdInConnection(CONNECTION_ID, DEFAULT_USER_ID))
                  .as("checking if exception is thrown when other user not found")
                  .isInstanceOf(IllegalArgumentException.class),
          () -> verify(connectionRepository, times(1)).findById(CONNECTION_ID));
    }
  }
}
