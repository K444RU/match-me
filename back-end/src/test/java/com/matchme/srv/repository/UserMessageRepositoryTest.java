package com.matchme.srv.repository;

import static com.matchme.srv.model.enums.UserState.ACTIVE;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventType;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserMessageRepositoryTest {

  @Autowired private UserMessageRepository userMessageRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private ConnectionRepository connectionRepository;

  @Autowired private EntityManager entityManager;

  private Connection connection;
  private User user1;
  private User user2;
  private MessageEventType readEventType;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setEmail("user1@example.com");
    user1.setState(ACTIVE);
    user1 = userRepository.save(user1);

    user2 = new User();
    user2.setEmail("user2@example.com");
    user2.setState(ACTIVE);
    user2 = userRepository.save(user2);

    // Get a reference to the existing "READ" event type (ID 3 from data-test.sql)
    readEventType = entityManager.getReference(MessageEventType.class, 3L);

    // Initialize connection (seems like it was missing)
    connection = Connection.builder().users(Set.of(user1, user2)).build();
    connection = connectionRepository.save(connection);
  }

  @Test
  @DisplayName("findByConnectionIdOrderByCreatedAtDesc should return page of UserMessages")
  void UserMessageRepository_FindByConnectionIdOrderByCreatedAtDesc_ReturnPageUserMessage() {

    // Arrange
    User user = new User();
    user.setEmail("test@example.com");
    user.setState(ACTIVE);
    user = userRepository.save(user);

    UserMessage message1 =
        UserMessage.builder()
            .connection(connection)
            .sender(user)
            .content("Message 1")
            .createdAt(Instant.now().minusSeconds(60))
            .build();

    UserMessage message2 =
        UserMessage.builder()
            .connection(connection)
            .sender(user)
            .content("Message 2")
            .createdAt(Instant.now())
            .build();

    userMessageRepository.save(message1);
    userMessageRepository.save(message2);

    entityManager.flush();

    Pageable pageable = PageRequest.of(0, 10);

    // Act
    Page<UserMessage> result =
        userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(connection.getId(), pageable);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getTotalElements()).isEqualTo(2);
    Assertions.assertThat(result.getContent()).hasSize(2);

    // Assert
    Assertions.assertThat(result).hasSize(2);
    Assertions.assertThat(result.getContent().get(0).getContent()).isEqualTo("Message 2");
    Assertions.assertThat(result.getContent().get(1).getContent()).isEqualTo("Message 1");
  }

  @Test
  @DisplayName("findMessagesToMarkAsRead should return only unread messages from other users")
  void findMessagesToMarkAsRead_ReturnsOnlyUnreadMessagesFromOthers() {
    // Arrange
    UserMessage message1Unread =
        UserMessage.builder()
            .connection(connection)
            .sender(user1)
            .content("Unread message from user1")
            .createdAt(Instant.now().minusSeconds(120))
            .build();
    userMessageRepository.save(message1Unread);

    UserMessage message2Read =
        UserMessage.builder()
            .connection(connection)
            .sender(user1)
            .content("Read message from user1")
            .createdAt(Instant.now().minusSeconds(60))
            .build();
    userMessageRepository.save(message2Read);

    MessageEvent readEventForMsg2 = new MessageEvent();
    readEventForMsg2.setMessage(message2Read);
    readEventForMsg2.setMessageEventType(readEventType);
    readEventForMsg2.setTimestamp(Instant.now());
    entityManager.persist(readEventForMsg2);

    UserMessage message3FromUser2 =
        UserMessage.builder()
            .connection(connection)
            .sender(user2)
            .content("Message from user2")
            .createdAt(Instant.now())
            .build();
    userMessageRepository.save(message3FromUser2);

    Connection otherConnection = connectionRepository.save(Connection.builder().build());
    UserMessage message4OtherConn =
        UserMessage.builder()
            .connection(otherConnection)
            .sender(user1)
            .content("Message in other connection")
            .createdAt(Instant.now().minusSeconds(30))
            .build();
    userMessageRepository.save(message4OtherConn);

    entityManager.flush();

    // --- Act ---
    List<UserMessage> result =
        userMessageRepository.findMessagesToMarkAsRead(connection.getId(), user2.getId());

    // --- Assert ---
    Assertions.assertThat(result).isNotNull().hasSize(1);

    Assertions.assertThat(result.get(0).getId()).isEqualTo(message1Unread.getId());
    Assertions.assertThat(result.get(0).getContent()).isEqualTo("Unread message from user1");
    Assertions.assertThat(result.get(0).getSender().getId())
        .isEqualTo(user1.getId()); // Verify sender is user1
  }

  @Test
  @DisplayName(
      "findMessagesToMarkAsRead should return empty list if no unread messages from others")
  void findMessagesToMarkAsRead_NoUnreadMessages_ReturnsEmptyList() {
    // Arrange
    // Create a message from user1, but mark it as read by user2
    UserMessage messageRead =
        UserMessage.builder()
            .connection(connection)
            .sender(user1)
            .content("Already read")
            .createdAt(Instant.now().minusSeconds(60))
            .build();
    userMessageRepository.save(messageRead);

    MessageEvent readEvent = new MessageEvent();
    readEvent.setMessage(messageRead);
    readEvent.setMessageEventType(readEventType);
    readEvent.setTimestamp(Instant.now());
    entityManager.persist(readEvent);

    // Create a message from user2 (should be ignored)
    UserMessage messageFromUser2 =
        UserMessage.builder()
            .connection(connection)
            .sender(user2)
            .content("From me")
            .createdAt(Instant.now())
            .build();
    userMessageRepository.save(messageFromUser2);

    entityManager.flush();

    // Act: Find messages for user2 to read
    List<UserMessage> result =
        userMessageRepository.findMessagesToMarkAsRead(connection.getId(), user2.getId());

    // Assert
    Assertions.assertThat(result).isNotNull().isEmpty();
  }
}
