package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserMessageRepositoryTest {

    @Autowired
    private UserMessageRepository userMessageRepository;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    private Connection testConnection;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("user1@example.com");
        testUser = userRepository.save(testUser);

        testConnection = new Connection();
        testConnection.getUsers().add(testUser);
        testConnection = connectionRepository.save(testConnection);

        UserMessage message1 = new UserMessage();
        message1.setUser(testUser);
        message1.setConnection(testConnection);
        message1.setContent("Message 1");
        message1.setCreatedAt(Instant.now().minus(Duration.ofMillis(1000)));
        userMessageRepository.save(message1);

        UserMessage message2 = new UserMessage();
        message2.setUser(testUser);
        message2.setConnection(testConnection);
        message2.setContent("Message 2");
        message2.setCreatedAt(Instant.now());
        userMessageRepository.save(message2);
    }

    @Test
    void testFindByConnectionIdOrderByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 10);

        // Fetch messages by connectionId
        Page<UserMessage> result = userMessageRepository.findByConnectionIdOrderByCreatedAtDesc(testConnection.getId(), pageable);

        // Verify the results
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("Message 2");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("Message 1");
    }
}
