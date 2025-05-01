package com.matchme.srv.publisher;

import com.matchme.srv.dto.graphql.ConnectionUpdateEvent;
import com.matchme.srv.model.connection.ConnectionUpdateMessage;
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

    private final Map<Long, Sinks.Many<ConnectionUpdateEvent>> sinks = new ConcurrentHashMap<>();

    public Flux<ConnectionUpdateEvent> getPublisher(Long userId) {
        return getSinkForUser(userId).asFlux();
    }

    public void publishUpdate(Long userId, ConnectionUpdateMessage message) {
        try {
            ConnectionUpdateEvent event = new ConnectionUpdateEvent(
                message.getAction(),
                message.getConnection()
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

    public void resetSinkForUser(Long userId) {
        sinks.put(userId, Sinks.many().multicast().onBackpressureBuffer());
        log.debug("Reset sink for user {}", userId);
    }
}