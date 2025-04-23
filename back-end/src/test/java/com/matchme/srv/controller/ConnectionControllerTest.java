package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.exception.GlobalExceptionHandler;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.matchme.srv.TestDataFactory.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ConnectionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ConnectionService connectionService;

    @Mock
    private MatchingService matchingService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ConnectionController connectionController;

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(connectionController)
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void dismissRecommendations_success() throws Exception {
        Long dismissedUserId = DEFAULT_TARGET_USER_ID;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);

        doNothing().when(matchingService).dismissedRecommendation(DEFAULT_USER_ID, dismissedUserId);

        mockMvc.perform(post("/connections/recommendations/" + dismissedUserId + "/dismiss")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(securityUtils).getCurrentUserId(authentication);
        verify(matchingService).dismissedRecommendation(DEFAULT_USER_ID, dismissedUserId);
        verifyNoMoreInteractions(matchingService);
        verifyNoInteractions(connectionService);
    }

    @Test
    void dismissRecommendations_whenServiceThrowsNotFound_shouldReturnNotFound() throws Exception {
        Long nonExistentDismissedUserId = INVALID_USER_ID;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);

        doThrow(new ResourceNotFoundException("User Profile not found with id " + nonExistentDismissedUserId))
                .when(matchingService).dismissedRecommendation(DEFAULT_USER_ID, nonExistentDismissedUserId);

        mockMvc.perform(post("/connections/recommendations/" + nonExistentDismissedUserId + "/dismiss")
                        .principal(authentication))
                .andExpect(status().isNotFound());

        verify(securityUtils).getCurrentUserId(authentication);
        verify(matchingService).dismissedRecommendation(DEFAULT_USER_ID, nonExistentDismissedUserId);
    }

    @Test
    void getConnections_success() throws Exception {
        ConnectionsDTO connectionsDTO = createSampleConnectionsDTO();

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.getConnections(DEFAULT_USER_ID)).thenReturn(connectionsDTO);

        mockMvc.perform(get("/connections").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active[*].userId", containsInAnyOrder(2, 3)))
                .andExpect(jsonPath("$.pendingIncoming[*].userId", containsInAnyOrder(4)))
                .andExpect(jsonPath("$.pendingOutgoing[*].userId", containsInAnyOrder(5)));

        verify(connectionService).getConnections(DEFAULT_USER_ID);
    }

    @Test
    void sendConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
                .thenReturn(DEFAULT_CONNECTION_ID);

        mockMvc.perform(post("/connections/requests/" + DEFAULT_TARGET_USER_ID)
                        .principal(authentication))
                .andExpect(status().isCreated());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void sendConnectionRequest_toSelf() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_USER_ID))
                .thenThrow(new IllegalStateException("Cannot send a connection request to yourself"));

        mockMvc.perform(post("/connections/requests/" + DEFAULT_USER_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_USER_ID);
    }

    @Test
    void sendConnectionRequest_pendingExists() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
                .thenThrow(new IllegalStateException("A pending request already exists from you to this user"));

        mockMvc.perform(post("/connections/requests/" + DEFAULT_TARGET_USER_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        Connection connection = new Connection();
        connection.setId(DEFAULT_CONNECTION_ID);
        when(connectionService.acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID))
                .thenReturn(connection);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_notPending() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        when(connectionService.acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID))
                .thenThrow(new IllegalStateException("Connection is not in PENDING state"));

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_notAuthorized() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        when(connectionService.acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID))
                .thenThrow(new IllegalStateException("You are not authorized to accept this request"));

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void rejectConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        Connection connection = new Connection();
        connection.setId(DEFAULT_CONNECTION_ID);
        when(connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID))
                .thenReturn(connection);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void rejectConnectionRequest_notPending() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        when(connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID))
                .thenThrow(new IllegalStateException("Connection is not in PENDING state"));

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void rejectConnectionRequest_notAuthorized() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        when(connectionService.rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID))
                .thenThrow(new IllegalStateException("You are not authorized to reject this request"));

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void disconnect_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        Connection connection = new Connection();
        connection.setId(DEFAULT_CONNECTION_ID);
        when(connectionService.disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
                .thenReturn(connection);

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isNoContent());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);
    }

    @Test
    void disconnect_notPartOfConnection() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        when(connectionService.disconnect(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID))
                .thenThrow(new IllegalStateException("You are not part of this connection"));

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void disconnect_notAccepted() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
                .thenThrow(new IllegalStateException("Connection is not in ACCEPTED state"));

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);
    }
}
