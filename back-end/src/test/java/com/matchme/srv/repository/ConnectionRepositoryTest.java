package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testFindConnectionsByUserId() {

        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setNumber("+37255566777");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setNumber("+37255566888");
        userRepository.save(user2);

        Connection connection = new Connection();
        connection.getUsers().add(user1);
        connection.getUsers().add(user2);
        connectionRepository.save(connection);

        List<Connection> connections = connectionRepository.findConnectionsByUserId(user1.getId());

        assertThat(connections).isNotEmpty();
        assertThat(connections.get(0).getUsers()).contains(user1, user2);
    }
}
