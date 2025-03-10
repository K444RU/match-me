package com.matchme.srv.service;

import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessValidationServiceTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @InjectMocks
    private AccessValidationService accessValidationService;

    @BeforeEach
    void setUp() {
        reset(connectionRepository);
    }

    @Test
    void ownerHasAccess() {
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(1L, 1L));
    }

    @Test
    void pendingRequestFromRequesterHasAccess() {
        when(connectionRepository.existsConnectionBetween(1L, 3L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(1L, 3L)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(1L, 3L));
        verify(connectionRepository).hasPendingConnectionRequest(1L, 3L);
    }

    @Test
    void pendingRequestToTargetHasAccess() {
        when(connectionRepository.existsConnectionBetween(1L, 4L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(1L, 4L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(4L, 1L)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(1L, 4L));
        verify(connectionRepository).hasPendingConnectionRequest(4L, 1L);
    }

    @Test
    void nonConnectedUserDenied() {
        when(connectionRepository.existsConnectionBetween(1L, 5L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(1L, 5L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(5L, 1L)).thenReturn(false);
        when(connectionRepository.isInRecommendations(1L, 5L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> accessValidationService.validateUserAccess(1L, 5L));
    }

    @Test
    void recommendationHasAccess() {
        when(connectionRepository.existsConnectionBetween(1L, 6L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(1L, 6L)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(6L, 1L)).thenReturn(false);
        when(connectionRepository.isInRecommendations(1L, 6L)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(1L, 6L));
        verify(connectionRepository).isInRecommendations(1L, 6L);
    }
}