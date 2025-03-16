package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.exception.GlobalExceptionHandler;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
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
    void getConnections_success() throws Exception {
        ConnectionsDTO connectionsDTO = createSampleConnectionsDTO();

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        when(connectionService.getConnections(DEFAULT_USER_ID)).thenReturn(connectionsDTO);

        mockMvc.perform(get("/connections").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", containsInAnyOrder(2, 3)))
                .andExpect(jsonPath("$.pendingIncoming", containsInAnyOrder(4)))
                .andExpect(jsonPath("$.pendingOutgoing", containsInAnyOrder(5)));

        verify(connectionService).getConnections(DEFAULT_USER_ID);
    }

    @Test
    void sendConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        doNothing().when(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(post("/connections/requests/" + DEFAULT_TARGET_USER_ID)
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void sendConnectionRequest_toSelf() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        doThrow(new IllegalStateException("Cannot send a connection request to yourself"))
                .when(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_USER_ID);

        mockMvc.perform(post("/connections/requests/" + DEFAULT_USER_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_USER_ID);
    }

    @Test
    void sendConnectionRequest_pendingExists() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        doThrow(new IllegalStateException("A pending request already exists from you to this user"))
                .when(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(post("/connections/requests/" + DEFAULT_TARGET_USER_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        doNothing().when(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_notPending() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        doThrow(new IllegalStateException("Connection is not in PENDING state"))
                .when(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void acceptConnectionRequest_notAuthorized() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        doThrow(new IllegalStateException("You are not authorized to accept this request"))
                .when(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void rejectConnectionRequest_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        doNothing().when(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void rejectConnectionRequest_notPending() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_TARGET_USER_ID);
        doThrow(new IllegalStateException("Connection is not in PENDING state"))
                .when(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_TARGET_USER_ID);
    }

    @Test
    void rejectConnectionRequest_notAuthorized() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        doThrow(new IllegalStateException("You are not authorized to reject this request"))
                .when(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);

        mockMvc.perform(patch("/connections/requests/" + DEFAULT_CONNECTION_ID + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void disconnect_success() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        doNothing().when(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);
    }

    @Test
    void disconnect_notPartOfConnection() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_UNAUTHORIZED_USER_ID);
        doThrow(new IllegalStateException("You are not part of this connection"))
                .when(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_UNAUTHORIZED_USER_ID);
    }

    @Test
    void disconnect_notAccepted() throws Exception {
        when(securityUtils.getCurrentUserId(authentication)).thenReturn(DEFAULT_USER_ID);
        doThrow(new IllegalStateException("Connection is not in ACCEPTED state"))
                .when(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);

        mockMvc.perform(delete("/connections/" + DEFAULT_CONNECTION_ID)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID);
    }
}
