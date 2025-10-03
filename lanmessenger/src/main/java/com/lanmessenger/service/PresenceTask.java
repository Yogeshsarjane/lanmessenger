package com.lanmessenger.service;

import com.lanmessenger.model.ChatMessage;
import com.lanmessenger.model.UserStatus;
import com.lanmessenger.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PresenceTask {

    // Define our time thresholds
    private static final long CONNECTING_THRESHOLD_SECONDS = 45;
    private static final long OFFLINE_THRESHOLD_SECONDS = 180; // 3 minutes

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceTask(UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 20000) // Run every 20 seconds
    public void checkUserPresence() {
        boolean listChanged = false;
        Instant now = Instant.now();

        // Loop through all users who have sent a heartbeat
        for (String username : userService.getHeartbeatMap().keySet()) {
            Instant lastHeartbeat = userService.getHeartbeatMap().get(username);
            long secondsSinceHeartbeat = ChronoUnit.SECONDS.between(lastHeartbeat, now);

            if (secondsSinceHeartbeat > OFFLINE_THRESHOLD_SECONDS) {
                // User is truly offline, remove them
                System.out.println(username + " is now OFFLINE. Last heartbeat was " + secondsSinceHeartbeat + "s ago.");
                userService.setUserOffline(username);
                userService.removeUserFromHeartbeat(username);

                // Send the LEAVE message
                ChatMessage leaveMessage = new ChatMessage();
                leaveMessage.setType(ChatMessage.MessageType.LEAVE);
                leaveMessage.setSender(username);
                messagingTemplate.convertAndSend("/topic/public", leaveMessage);

                listChanged = true;

            } else if (secondsSinceHeartbeat > CONNECTING_THRESHOLD_SECONDS) {
                // User is in the grace period
                System.out.println(username + " is now CONNECTING. Last heartbeat was " + secondsSinceHeartbeat + "s ago.");
                // This status should already be set in the DB, but we ensure it
                // We don't need to do anything here, just let them be "CONNECTING"
            }
        }

        if (listChanged) {
            userService.broadcastUserList();
        }
    }
}