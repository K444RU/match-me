package com.matchme.srv.controller;

import com.matchme.srv.dto.response.ChatMessageResponseDTO;
import com.matchme.srv.dto.response.ChatPreviewResponseDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SecurityUtils securityUtils;

    @GetMapping("/previews")
    public List<ChatPreviewResponseDTO> getChatPreviews(Authentication authentication) {
        Long userId = securityUtils.getCurrentUserId(authentication);
        User user = userService.getUser(userId);
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

}
