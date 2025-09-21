package com.lanmessenger.controller;

import com.lanmessenger.model.ChatMessageLog;
import com.lanmessenger.repository.ChatMessageLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageLogRepository chatLogRepository;

    public ChatController(ChatMessageLogRepository chatLogRepository) {
        this.chatLogRepository = chatLogRepository;
    }

    @GetMapping("/history/group")
    public ResponseEntity<List<ChatMessageLog>> getRecentGroupChatHistory() {
        Pageable limit = PageRequest.of(0, 20);
        List<ChatMessageLog> history = chatLogRepository.findByRecipientOrderByTimestampDesc("group", limit);
        Collections.reverse(history);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/private/{otherUser}")
    public ResponseEntity<List<ChatMessageLog>> getPrivateChatHistory(
            Principal principal,
            @PathVariable String otherUser) {

        String currentUser = principal.getName();
        Pageable limit = PageRequest.of(0, 20);
        List<ChatMessageLog> history = chatLogRepository.findPrivateChatHistory(currentUser, otherUser, limit);
        Collections.reverse(history);
        return ResponseEntity.ok(history);
    }
}