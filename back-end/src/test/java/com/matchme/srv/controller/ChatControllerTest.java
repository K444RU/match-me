package com.matchme.srv.controller;

import static com.matchme.srv.TestDataFactory.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

  private MockMvc mockMvc;

  @Mock private ChatService chatService;

  @Mock private UserQueryService queryService;

  @Mock private Authentication authentication;

  @Mock private SecurityUtils securityUtils;

  @InjectMocks private ChatController chatController;

  private static final Long DEFAULT_CONNECTION_ID = 101L;
  private static final Long DEFAULT_MESSAGE_ID = 456L;
  private static final String DEFAULT_MESSAGE_CONTENT = "Hello, this is a test message.";

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(chatController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    when(securityUtils.getCurrentUserId(any(Authentication.class)))
        .thenAnswer(
            invocation -> {
              UserDetailsImpl userDetails =
                  (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
              return userDetails.getId();
            });
    UserDetailsImpl userDetails =
        new UserDetailsImpl(DEFAULT_USER_ID, DEFAULT_EMAIL, "password", Collections.emptySet());
    when(authentication.getPrincipal()).thenReturn(userDetails);
  }

  @Test
  @DisplayName("Should return chat previews")
  void getChatPreviews_WhenRequested_ReturnsChatPreviews() throws Exception {
    User mockUser = createUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    when(queryService.getUser(DEFAULT_USER_ID)).thenReturn(mockUser);

    ChatPreviewResponseDTO chatPreview = new ChatPreviewResponseDTO();
    chatPreview.setConnectionId(DEFAULT_CONNECTION_ID);
    chatPreview.setConnectedUserId(DEFAULT_TARGET_USER_ID);
    chatPreview.setConnectedUserAlias(DEFAULT_TARGET_ALIAS);
    chatPreview.setLastMessageContent(DEFAULT_MESSAGE_CONTENT);
    chatPreview.setLastMessageTimestamp(Instant.now());
    chatPreview.setUnreadMessageCount(3);

    when(chatService.getChatPreviews(anyLong())).thenReturn(List.of(chatPreview));

    mockMvc
        .perform(
            get("/api/chats/previews")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(1)),
            jsonPath("$[0].connectionId", is(DEFAULT_CONNECTION_ID.intValue())),
            jsonPath("$[0].connectedUserId", is(DEFAULT_TARGET_USER_ID.intValue())),
            jsonPath("$[0].connectedUserAlias", is(DEFAULT_TARGET_ALIAS)),
            jsonPath("$[0].lastMessageContent", is(DEFAULT_MESSAGE_CONTENT)),
            jsonPath("$[0].unreadMessageCount", is(3)));
  }

  @Test
  @DisplayName("Should return chat messages")
  void getChatMessages_WhenRequested_ReturnsChatMessages() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    ChatMessageResponseDTO chatMessage =
        new ChatMessageResponseDTO(
            DEFAULT_MESSAGE_ID,
            DEFAULT_CONNECTION_ID,
            DEFAULT_USER_ID,
            DEFAULT_TARGET_ALIAS,
            DEFAULT_MESSAGE_CONTENT,
            Instant.now());

    Page<ChatMessageResponseDTO> messagePage = new PageImpl<>(List.of(chatMessage), pageable, 1);
    when(chatService.getChatMessages(anyLong(), anyLong(), any(Pageable.class)))
        .thenReturn(messagePage);

    mockMvc
        .perform(
            get("/api/chats/{connectionId}/messages", DEFAULT_CONNECTION_ID)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "10"))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.content", hasSize(1)),
            jsonPath("$.content[0].connectionId", is(DEFAULT_CONNECTION_ID.intValue())),
            jsonPath("$.content[0].messageId", is(DEFAULT_MESSAGE_ID.intValue())),
            jsonPath("$.content[0].senderAlias", is(DEFAULT_TARGET_ALIAS)),
            jsonPath("$.content[0].content", is(DEFAULT_MESSAGE_CONTENT)));
  }

  @Test
  @DisplayName("Should mark chat messages as read")
  void readChatMessages_WhenRequested_MarksMessagesAsRead() throws Exception {
    ChatPreviewResponseDTO previewResponse = new ChatPreviewResponseDTO();
    previewResponse.setConnectionId(DEFAULT_CONNECTION_ID);
    previewResponse.setConnectedUserId(DEFAULT_TARGET_USER_ID);
    previewResponse.setConnectedUserAlias(DEFAULT_TARGET_ALIAS);
    previewResponse.setLastMessageContent(DEFAULT_MESSAGE_CONTENT);
    previewResponse.setLastMessageTimestamp(Instant.now());
    previewResponse.setUnreadMessageCount(0);

    when(chatService.markMessagesAsRead(DEFAULT_CONNECTION_ID, DEFAULT_USER_ID))
        .thenReturn(previewResponse);

    mockMvc
        .perform(
            post("/api/chats/{connectionId}/messages/read", DEFAULT_CONNECTION_ID)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.connectionId", is(DEFAULT_CONNECTION_ID.intValue())),
            jsonPath("$.connectedUserId", is(DEFAULT_TARGET_USER_ID.intValue())),
            jsonPath("$.connectedUserAlias", is(DEFAULT_TARGET_ALIAS)),
            jsonPath("$.lastMessageContent", is(DEFAULT_MESSAGE_CONTENT)),
            jsonPath("$.unreadMessageCount", is(0)));
  }
}
