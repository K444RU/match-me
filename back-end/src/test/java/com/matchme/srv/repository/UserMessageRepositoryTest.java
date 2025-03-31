package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class UserMessageRepositoryTest {

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Test
    void UserMessageRepository_FindByConnectionIdOrderByCreatedAtDesc_ReturnPageUserMessage() {

        // Arrange
        Connection connection = Connection.builder().build();
        connection = connectionRepository.save(connection);

        User user = new User();
        user.setEmail("test@example.com");
        user = userRepository.save(user);

        UserMessage message1 = UserMessage.builder()
                .connection(connection)
                .sender(user)
                .content("Message 1")
                .createdAt(Instant.now().minusSeconds(60))
                .build();

        UserMessage message2 = UserMessage.builder()
                .connection(connection)
                .sender(user)
                .content("Message 2")
                .createdAt(Instant.now())
                .build();

        userMessageRepository.save(message1);
        userMessageRepository.save(message2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<UserMessage> result =
                userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(1L, pageable);

        // Assert
        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.getContent().get(0).getContent()).isEqualTo("Message 2");
        Assertions.assertThat(result.getContent().get(1).getContent()).isEqualTo("Message 1");

    }
}
