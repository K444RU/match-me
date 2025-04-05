package com.matchme.srv.controller;

import com.matchme.srv.dto.request.MessagesSendRequestDTO;
import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.user.UserQueryService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserQueryService queryService;
    private final SecurityUtils securityUtils;

    /**
     * Retrieves a list of chat previews for the authenticated user.
     *<p>
     * Keep this endpoint because it provides a quick snapshot of chats
     * during the initial page load, without requiring a WebSocket connection.
     * WebSockets can then handle real-time updates after the initial load.
     *<p>
     * Keeping both REST and WebSocket approaches ensures flexibility and
     * improves the user experience.
     *
     * @param authentication The authentication object to retrieve the current user's details.
     * @return A list of `ChatPreviewResponseDTO` objects containing chat preview details.
     */
    @GetMapping("/previews")
    public List<ChatPreviewResponseDTO> getChatPreviews(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        User user = queryService.getUser(userId);
        return chatService.getChatPreviews(user.getId());
    }

    @GetMapping("/{connectionId}/messages")
    public Page<ChatMessageResponseDTO> getChatMessages(
            @PathVariable Long connectionId,
            Pageable pageable,
            Authentication authentication
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return chatService.getChatMessages(connectionId, userId, pageable);
    }

    /**
     * Purpose of this endpoint is to test the current getChatPreviews endpoint
     * by manually adding messages to a connectionId and making sure that the endpoint works.
     * Could be removed after the final chat functionality is on track
     *
     * @param connectionId The ID of the connection to which the message will be added.
     * @param chatMessageRequestDTO The request body containing the message content.
     * @param authentication The authentication object to retrieve the current user's details.
     * @return ChatMessageResponseDTO containing the details of the saved message.
     */
    @PostMapping("/{connectionId}/messages")
    public ChatMessageResponseDTO sendChatMessage(
            @PathVariable Long connectionId,
            @RequestBody MessagesSendRequestDTO chatMessageRequestDTO,
            Authentication authentication
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return chatService.saveMessage(connectionId, userId, chatMessageRequestDTO.getContent(), Instant.now(), false);
    }

    /**
     * Marks all messages as read for a given connectionId
     * @param connectionId The ID of the connection to which the message will be added.
     * @param authentication The authentication object to retrieve the current user's details.
     */
    @PostMapping("/{connectionId}/messages/read")
    public ChatPreviewResponseDTO readChatMessages(
            @PathVariable Long connectionId,
            Authentication authentication
    ) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        return chatService.markMessagesAsRead(connectionId, userId);
    }
}
