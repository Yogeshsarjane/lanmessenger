package com.lanmessenger.service; // Or your correct package

import com.lanmessenger.model.ChatMessage; // <-- IMPORT this
import com.lanmessenger.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;   // <-- IMPORT this
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate; // <-- ADD DEPENDENCY

    // ✅ Update the constructor to accept the new dependency
    public WebSocketEventListener(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        // ... inside the handleWebSocketDisconnectListener method ...

        if (username != null) {
            log.info("User Disconnected: {}", username);

            // Mark user as offline in the service
            userService.setUserOffline(username);

            // ✅ CREATE AND SEND THE LEAVE MESSAGE USING THE ENUM
            ChatMessage leaveMessage = new ChatMessage();
            // Use the enum constant, not a string
            leaveMessage.setType(ChatMessage.MessageType.LEAVE);
            leaveMessage.setSender(username);
            messagingTemplate.convertAndSend("/topic/public", leaveMessage);

            // Broadcast the updated user list
            userService.broadcastUserList();
        }
    }
}