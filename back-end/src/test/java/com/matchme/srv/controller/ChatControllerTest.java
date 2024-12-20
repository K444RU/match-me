package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChatControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void testGetChatPreviews() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user1@example.com", "password", Collections.emptySet());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user1@example.com");
        when(userService.getUser(1L)).thenReturn(mockUser);

        ChatPreviewResponseDTO chatPreview = new ChatPreviewResponseDTO();
        chatPreview.setConnectionId(101L);
        chatPreview.setConnectedUserId(2L);
        chatPreview.setConnectedUserAlias("TestNickName123321");
        chatPreview.setLastMessageContent("Hello!");
        chatPreview.setLastMessageTimestamp(Instant.now());
        chatPreview.setUnreadMessageCount(3);

        when(chatService.getChatPreviews(anyLong())).thenReturn(List.of(chatPreview));

        mockMvc.perform(get("/api/chats/previews")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].connectionId", is(101)))
                .andExpect(jsonPath("$[0].connectedUserId", is(2)))
                .andExpect(jsonPath("$[0].connectedUserAlias", is("TestNickName123321")))
                .andExpect(jsonPath("$[0].lastMessageContent", is("Hello!")))
                .andExpect(jsonPath("$[0].unreadMessageCount", is(3)));
    }

    @Test
    void testGetChatMessages() throws Exception {
        UserDetailsImpl userDetails = new UserDetailsImpl(1L, "user1@example.com", "password", Collections.emptySet());
        when(authentication.getPrincipal()).thenReturn(userDetails);

        Pageable pageable = PageRequest.of(0, 10);
        ChatMessageResponseDTO chatMessage = new ChatMessageResponseDTO(
                201L,
                101L,
                "TestNickName123321",
                "Hello, this is a test message.",
                Instant.now()
        );

        Page<ChatMessageResponseDTO> messagePage = new PageImpl<>(List.of(chatMessage), pageable, 1);
        when(chatService.getChatMessages(anyLong(), anyLong(), any(Pageable.class))).thenReturn(messagePage);

        mockMvc.perform(get("/api/chats/{connectionId}/messages", 101L)
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].connectionId", is(101)))
                .andExpect(jsonPath("$.content[0].messageId", is(201)))
                .andExpect(jsonPath("$.content[0].senderAlias", is("TestNickName123321")))
                .andExpect(jsonPath("$.content[0].content", is("Hello, this is a test message.")));
    }

}
