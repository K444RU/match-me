package com.matchme.srv.publisher;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.dto.graphql.UserGraphqlDTO;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionPublisher {

    private final UserQueryService userQueryService;
    private final Map<Long, Sinks.Many<ConnectionUpdateEvent>> sinks = new ConcurrentHashMap<>();

    public Flux<ConnectionUpdateEvent> getPublisher(Long userId) {
        return getSinkForUser(userId).asFlux();
    }

    public void publishUpdate(Long userId, ConnectionUpdateMessage message) {
        try {
            User otherUser = userQueryService.getUser(message.getConnection().getUserId());
            UserGraphqlDTO userDTO = new UserGraphqlDTO(otherUser);
            
            ConnectionUpdateEvent event = new ConnectionUpdateEvent(
                message.getAction(),
                message.getConnection().getConnectionId().toString(),
                userDTO
            );
            
            getSinkForUser(userId).tryEmitNext(event);
        } catch (Exception e) {
            log.error("Error publishing connection update: {}", e.getMessage(), e);
        }
    }

    private Sinks.Many<ConnectionUpdateEvent> getSinkForUser(Long userId) {
        return sinks.computeIfAbsent(userId, id -> 
            Sinks.many().multicast().onBackpressureBuffer());
    }
}