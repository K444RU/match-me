package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Set;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindConnectionsByUserId() {

        // Arrange
        User user1 = new User();
        user1.setEmail("user1@example.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        userRepository.save(user2);

        Connection connection = Connection.builder().users(Set.of(user1, user2)).build();
        connectionRepository.save(connection);

        // Act
        List<Connection> connections = connectionRepository.findConnectionsByUserId(user1.getId());

        // Assert
        Assertions.assertThat(connections).isNotEmpty();
        Assertions.assertThat(connections.get(0).getUsers()).contains(user1, user2);
    }
}
