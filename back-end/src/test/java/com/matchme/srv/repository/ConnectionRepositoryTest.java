package com.matchme.srv.repository;

import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import static com.matchme.srv.model.enums.UserState.ACTIVE;
import com.matchme.srv.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ConnectionRepositoryTest {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Connection connection;

    @BeforeEach
    void setUp() {
        user1 = User.builder().email("user1@example.com").state(ACTIVE).build();
        userRepository.save(user1);

        user2 = User.builder().email("user2@example.com").state(ACTIVE).build();
        userRepository.save(user2);

        connection = Connection.builder().build();
        connection.getUsers().add(user1);
        connection.getUsers().add(user2);
        connectionRepository.save(connection);
    }

    @Test
    void ConnectionRepository_FindConnectionsById_ReturnListConnection() {
        ConnectionState initialState = ConnectionState.builder()
                .connection(connection)
                .user(user1)
                .status(ConnectionStatus.PENDING)
                .requesterId(user1.getId())
                .targetId(user2.getId())
                .timestamp(LocalDateTime.now())
                .build();
        connection.getConnectionStates().add(initialState);
        connectionRepository.save(connection);

        List<Connection> connections = connectionRepository.findConnectionsByUserId(user1.getId());

        assertThat(connections).hasSize(1);
        assertThat(connections.getFirst().getUsers()).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void ConnectionRepository_ExistsConnectionBetween_ReturnFalse() {
        User user3 = User.builder().email("user3@example.com").state(ACTIVE).build();
        userRepository.save(user3);

        boolean result = connectionRepository.existsConnectionBetween(user1.getId(), user3.getId());

        assertThat(result).isFalse();
    }

    @Test
    void ConnectionRepository_ExistsConnectionBetween_ReturnTrue() {
        ConnectionState acceptedState = ConnectionState.builder()
                .connection(connection)
                .user(user1)
                .status(ConnectionStatus.ACCEPTED)
                .requesterId(user1.getId())
                .targetId(user2.getId())
                .timestamp(LocalDateTime.now())
                .build();
        connection.getConnectionStates().add(acceptedState);
        connectionRepository.save(connection);

        boolean result = connectionRepository.existsConnectionBetween(user1.getId(), user2.getId());

        assertThat(result).isTrue();
    }

    @Test
    void findConnectionBetween_connectionExists_returnsConnection() {
        Connection foundConnection = connectionRepository.findConnectionBetween(user1.getId(), user2.getId());

        assertThat(foundConnection).isNotNull();
        assertThat(foundConnection.getUsers()).containsExactlyInAnyOrder(user1, user2);
    }

    @Test
    void findConnectionBetween_noConnection_returnsNull() {
        User user3 = User.builder().email("user3@example.com").state(ACTIVE).build();
        userRepository.save(user3);

        Connection foundConnection = connectionRepository.findConnectionBetween(user1.getId(), user3.getId());

        assertThat(foundConnection).isNull();
    }

    @Test
    void hasPendingConnectionRequest_pendingRequestExists_returnsTrue() {
        ConnectionState pendingState = ConnectionState.builder()
                .connection(connection)
                .user(user1)
                .status(ConnectionStatus.PENDING)
                .requesterId(user1.getId())
                .targetId(user2.getId())
                .timestamp(LocalDateTime.now())
                .build();
        connection.getConnectionStates().add(pendingState);
        connectionRepository.save(connection);

        boolean result = connectionRepository.hasPendingConnectionRequest(user1.getId(), user2.getId());

        assertThat(result).isTrue();
    }

}
