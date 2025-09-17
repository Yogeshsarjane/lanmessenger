package com.lanmessenger.controller;

import com.lanmessenger.model.ChatMessageLog;
import com.lanmessenger.model.FileLog;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.repository.ChatMessageLogRepository;
import com.lanmessenger.repository.FileLogRepository;
import com.lanmessenger.service.AppConfigService;
import com.lanmessenger.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest; // <-- IMPORT
import org.springframework.data.domain.Pageable;    // <-- IMPORT
import java.security.Principal;                   // <-- IMPORT

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // 1. Declare final fields for all your dependencies.
    private final UserService userService;
    private final AppConfigService appConfigService;
    private final ChatMessageLogRepository chatLogRepository;
    private final FileLogRepository fileLogRepository;

    // 2. Create one constructor for Spring to inject all dependencies.
    // The @Autowired annotation is optional here, but good for clarity.
    public AdminController(UserService userService,
                           AppConfigService appConfigService,
                           ChatMessageLogRepository chatLogRepository,
                           FileLogRepository fileLogRepository) {
        this.userService = userService;
        this.appConfigService = appConfigService;
        this.chatLogRepository = chatLogRepository;
        this.fileLogRepository = fileLogRepository;
    }

    // 3. All your existing endpoint methods remain exactly the same.
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

    @GetMapping("/history/chat/{username}")
    public List<ChatMessageLog> getChatHistory(@PathVariable String username) {
        return chatLogRepository.findChatHistoryForUser(username);
    }

    @GetMapping("/history/files/{username}")
    public List<FileLog> getFileHistory(@PathVariable String username) {
        return fileLogRepository.findFileHistoryForUser(username);
    }

    @GetMapping("/history/groupchat")
    public ResponseEntity<List<ChatMessageLog>> getRecentGroupChatHistory() {
        List<ChatMessageLog> history = chatLogRepository.findFirst50ByRecipientOrderByTimestampDesc("group");
        Collections.reverse(history);
        return ResponseEntity.ok(history);
    }

    // ✅ ADD THIS NEW ENDPOINT
    @GetMapping("/history/privatechat/{otherUser}")
    public ResponseEntity<List<ChatMessageLog>> getPrivateChatHistory(
            Principal principal,
            @PathVariable String otherUser) {

        // Get the currently logged-in admin's username
        String currentUser = principal.getName();

        // We'll fetch the 20 most recent messages
        Pageable limit = PageRequest.of(0, 20);

        // Call the new repository method
        List<ChatMessageLog> history = chatLogRepository.findPrivateChatHistory(currentUser, otherUser, limit);

        // The messages are newest-first, so we reverse them for correct chatbox order
        Collections.reverse(history);

        return ResponseEntity.ok(history);
    }
}