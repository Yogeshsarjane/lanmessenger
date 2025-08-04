package com.lanmessenger.controller;

import com.lanmessenger.model.ChatMessageLog;
import com.lanmessenger.model.FileLog;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.repository.ChatMessageLogRepository;
import com.lanmessenger.repository.FileLogRepository;
import com.lanmessenger.service.AppConfigService;
import com.lanmessenger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private ChatMessageLogRepository chatLogRepository; // <-- Inject Chat Log Repo

    @Autowired
    private FileLogRepository fileLogRepository; // <-- Inject File Log Repo

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userService.getAdminUserList();
    }

    @GetMapping("/settings/visibility")
    public Map<String, String> getVisibility() {
        return Collections.singletonMap("visibility", appConfigService.getUserListVisibility().name());
    }

    @PostMapping("/settings/visibility")
    public ResponseEntity<Void> setVisibility(@RequestBody Map<String, String> payload) {
        try {
            String visibilityStr = payload.get("visibility");
            AppConfigService.Visibility visibility = AppConfigService.Visibility.valueOf(visibilityStr.toUpperCase());
            appConfigService.setUserListVisibility(visibility);
            userService.broadcastUserList();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // --- NEW ENDPOINT for fetching a user's chat history ---
    @GetMapping("/history/chat/{username}")
    public List<ChatMessageLog> getChatHistory(@PathVariable String username) {
        return chatLogRepository.findChatHistoryForUser(username);
    }

    // --- NEW ENDPOINT for fetching a user's file history ---
    @GetMapping("/history/files/{username}")
    public List<FileLog> getFileHistory(@PathVariable String username) {
        return fileLogRepository.findFileHistoryForUser(username);
    }
}
