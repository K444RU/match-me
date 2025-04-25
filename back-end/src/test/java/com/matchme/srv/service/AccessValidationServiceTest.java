package com.matchme.srv.service;

import com.matchme.srv.repository.ConnectionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.matchme.srv.TestDataFactory.*;
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

    @BeforeEach
    void setUp() {
        reset(connectionRepository);
    }

    @Test
    void ownerHasAccess() {
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_OWNER));
    }

    @Test
    void pendingRequestFromRequesterHasAccess() {
        when(connectionRepository.existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_REQUESTER)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_REQUESTER)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_REQUESTER));
        verify(connectionRepository).hasPendingConnectionRequest(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_REQUESTER);
    }

    @Test
    void pendingRequestToTargetHasAccess() {
        when(connectionRepository.existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_TARGET)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_TARGET)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_PENDING_TARGET, TEST_USER_ID_OWNER)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_PENDING_TARGET));
        verify(connectionRepository).hasPendingConnectionRequest(TEST_USER_ID_PENDING_TARGET, TEST_USER_ID_OWNER);
    }

    @Test
    void connectedUsersHaveAccess() {
        when(connectionRepository.existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_CONNECTED)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_CONNECTED));
        verify(connectionRepository).existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_CONNECTED);
        verify(connectionRepository, never()).hasPendingConnectionRequest(anyLong(), anyLong());
        verifyNoInteractions(matchingService);
    }

    @Test
    void nonConnectedUserDenied() {
        when(connectionRepository.existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_NON_CONNECTED)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_OWNER, TEST_USER_ID_NON_CONNECTED)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_NON_CONNECTED, TEST_USER_ID_OWNER)).thenReturn(false);
        when(matchingService.isRecommended(TEST_USER_ID_OWNER, TEST_USER_ID_NON_CONNECTED)).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_NON_CONNECTED));
    }

    @Test
    void recommendationHasAccess() {
        when(connectionRepository.existsConnectionBetween(TEST_USER_ID_OWNER, TEST_USER_ID_RECOMMENDED)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_OWNER, TEST_USER_ID_RECOMMENDED)).thenReturn(false);
        when(connectionRepository.hasPendingConnectionRequest(TEST_USER_ID_RECOMMENDED, TEST_USER_ID_OWNER)).thenReturn(false);
        when(matchingService.isRecommended(TEST_USER_ID_OWNER, TEST_USER_ID_RECOMMENDED)).thenReturn(true);
        assertDoesNotThrow(() -> accessValidationService.validateUserAccess(TEST_USER_ID_OWNER, TEST_USER_ID_RECOMMENDED));
        verify(matchingService).isRecommended(TEST_USER_ID_OWNER, TEST_USER_ID_RECOMMENDED);
    }
}