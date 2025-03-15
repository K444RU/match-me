package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ConnectionsDTO;
import com.matchme.srv.exception.GlobalExceptionHandler;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.security.jwt.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        Long currentUserId = 1L;
        ConnectionsDTO connectionsDTO = new ConnectionsDTO(
                List.of(2L, 3L),
                List.of(4L),
                List.of(5L)
        );

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        when(connectionService.getConnections(currentUserId)).thenReturn(connectionsDTO);

        mockMvc.perform(get("/connections/")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active", containsInAnyOrder(2, 3)))
                .andExpect(jsonPath("$.pendingIncoming", containsInAnyOrder(4)))
                .andExpect(jsonPath("$.pendingOutgoing", containsInAnyOrder(5)));

        verify(connectionService).getConnections(currentUserId);
    }

    @Test
    void sendConnectionRequest_success() throws Exception {
        Long currentUserId = 1L;
        Long targetUserId = 2L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doNothing().when(connectionService).sendConnectionRequest(currentUserId, targetUserId);

        mockMvc.perform(get("/connections/request/" + targetUserId)
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).sendConnectionRequest(currentUserId, targetUserId);
    }

    @Test
    void sendConnectionRequest_toSelf() throws Exception {
        Long currentUserId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("Cannot send a connection request to yourself"))
                .when(connectionService).sendConnectionRequest(currentUserId, currentUserId);

        mockMvc.perform(get("/connections/request/" + currentUserId)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(currentUserId, currentUserId);
    }

    @Test
    void sendConnectionRequest_pendingExists() throws Exception {
        Long currentUserId = 1L;
        Long targetUserId = 2L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("A pending request already exists from you to this user"))
                .when(connectionService).sendConnectionRequest(currentUserId, targetUserId);

        mockMvc.perform(get("/connections/request/" + targetUserId)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).sendConnectionRequest(currentUserId, targetUserId);
    }

    @Test
    void acceptConnectionRequest_success() throws Exception {
        Long currentUserId = 2L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doNothing().when(connectionService).acceptConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/accept")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).acceptConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void acceptConnectionRequest_notPending() throws Exception {
        Long currentUserId = 2L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("Connection is not in PENDING state"))
                .when(connectionService).acceptConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void acceptConnectionRequest_notAuthorized() throws Exception {
        Long currentUserId = 3L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("You are not authorized to accept this request"))
                .when(connectionService).acceptConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/accept")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).acceptConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void rejectConnectionRequest_success() throws Exception {
        Long currentUserId = 2L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doNothing().when(connectionService).rejectConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/reject")
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).rejectConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void rejectConnectionRequest_notPending() throws Exception {
        Long currentUserId = 2L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("Connection is not in PENDING state"))
                .when(connectionService).rejectConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void rejectConnectionRequest_notAuthorized() throws Exception {
        Long currentUserId = 3L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("You are not authorized to reject this request"))
                .when(connectionService).rejectConnectionRequest(connectionId, currentUserId);

        mockMvc.perform(patch("/connections/requests/" + connectionId + "/reject")
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).rejectConnectionRequest(connectionId, currentUserId);
    }

    @Test
    void disconnect_success() throws Exception {
        Long currentUserId = 1L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doNothing().when(connectionService).disconnect(connectionId, currentUserId);

        mockMvc.perform(delete("/connections/" + connectionId)
                        .principal(authentication))
                .andExpect(status().isOk());

        verify(connectionService).disconnect(connectionId, currentUserId);
    }

    @Test
    void disconnect_notPartOfConnection() throws Exception {
        Long currentUserId = 3L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("You are not part of this connection"))
                .when(connectionService).disconnect(connectionId, currentUserId);

        mockMvc.perform(delete("/connections/" + connectionId)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(connectionId, currentUserId);
    }

    @Test
    void disconnect_notAccepted() throws Exception {
        Long currentUserId = 1L;
        Long connectionId = 1L;

        when(securityUtils.getCurrentUserId(authentication)).thenReturn(currentUserId);
        doThrow(new IllegalStateException("Connection is not in ACCEPTED state"))
                .when(connectionService).disconnect(connectionId, currentUserId);

        mockMvc.perform(delete("/connections/" + connectionId)
                        .principal(authentication))
                .andExpect(status().isBadRequest());

        verify(connectionService).disconnect(connectionId, currentUserId);
    }
}
