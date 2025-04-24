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

    @Mock
    private MatchingService matchingService;

    @InjectMocks
    private AccessValidationService accessValidationService;

    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private static final Long USER_ID_3 = 3L;
    private static final Long USER_ID_4 = 4L;
    private static final Long USER_ID_5 = 5L;
    private static final Long USER_ID_6 = 6L;

    @BeforeEach
    void setUp() {
        reset(connectionRepository);
    }

    @Test
    void ownerHasAccess() {
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_1));
    }

    @Test
    void pendingRequestFromRequesterHasAccess() {
        when(connectionRepository.existsConnectionBetween(USER_ID_1, USER_ID_3)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_1, USER_ID_3)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_3));
        verify(connectionRepository).hasPendingConnectionRequest(USER_ID_1, USER_ID_3);
    }

    @Test
    void pendingRequestToTargetHasAccess() {
        when(connectionRepository.existsConnectionBetween(USER_ID_1, USER_ID_4)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_1, USER_ID_4)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_4, USER_ID_1)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_4));
        verify(connectionRepository).hasPendingConnectionRequest(USER_ID_4, USER_ID_1);
    }

    @Test
    void connectedUsersHaveAccess() {
        when(connectionRepository.existsConnectionBetween(USER_ID_1, USER_ID_2)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_2));
        verify(connectionRepository).existsConnectionBetween(USER_ID_1, USER_ID_2);
        verify(connectionRepository, never()).hasPendingConnectionRequest(anyLong(), anyLong());
        verifyNoInteractions(matchingService);
    }

    @Test
    void nonConnectedUserDenied() {
        when(connectionRepository.existsConnectionBetween(USER_ID_1, USER_ID_5)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_1, USER_ID_5)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_5, USER_ID_1)).thenReturn(false);
        when(matchingService.isRecommended(USER_ID_1, USER_ID_5)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_5));
    }

    @Test
    void recommendationHasAccess() {
        when(connectionRepository.existsConnectionBetween(USER_ID_1, USER_ID_6)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_1, USER_ID_6)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(USER_ID_6, USER_ID_1)).thenReturn(false);
        when(matchingService.isRecommended(USER_ID_1, USER_ID_6)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(USER_ID_1, USER_ID_6));
        verify(matchingService).isRecommended(USER_ID_1, USER_ID_6);
    }
}