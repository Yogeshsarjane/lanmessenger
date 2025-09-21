package com.lanmessenger.service;

import com.lanmessenger.model.ChatMessage;
import com.lanmessenger.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PresenceCleanupTask {

    private static final long GRACE_PERIOD_SECONDS = 45;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceCleanupTask(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    // Run this check every 15 seconds
    @Scheduled(fixedRate = 15000)
    public void cleanupUnstableUsers() {
        Instant now = Instant.now();
        userService.getUnstableUsers().forEach((username, disconnectTime) -> {
            long secondsSinceDisconnect = ChronoUnit.SECONDS.between(disconnectTime, now);

            if (secondsSinceDisconnect > GRACE_PERIOD_SECONDS) {
                System.out.println("Grace period for " + username + " expired. Setting to OFFLINE.");

                // Mark user as offline
                userService.setUserOffline(username);

                // Create and send the LEAVE message
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setType(ChatMessage.MessageType.LEAVE);
                leaveMessage.setSender(username);
                messagingTemplate.convertAndSend("/topic/public", leaveMessage);

                // Broadcast the final user list
                userService.broadcastUserList();
            }
        });
    }
}