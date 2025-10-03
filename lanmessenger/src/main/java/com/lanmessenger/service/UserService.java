package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.model.UserStatus;
import com.lanmessenger.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AppConfigService appConfigService;

    // This map stores the timestamp of the last heartbeat received from each user.
    private final Map<String, Instant> userHeartbeats = new ConcurrentHashMap<>();

    public UserService(UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate,
                       AppConfigService appConfigService) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.appConfigService = appConfigService;
    }

    /**
     * Called when a user first connects or sends a heartbeat.
     * Ensures they are marked as ONLINE.
     */
    public void updateUserHeartbeat(String username) {
        userHeartbeats.put(username, Instant.now());
        User user = userRepository.findByUsername(username);
        if (user != null && user.getStatus() != UserStatus.ONLINE) {
            user.setStatus(UserStatus.ONLINE);
            userRepository.save(user);
        }
    }

    /**
     * Called by the scheduled task when a user's heartbeat is late.
     */
    public void setUserConnecting(String username) {
        User user = userRepository.findByUsername(username);
        // Only set to CONNECTING if they were previously ONLINE
        if (user != null && user.getStatus() == UserStatus.ONLINE) {
            user.setStatus(UserStatus.CONNECTING);
            userRepository.save(user);
        }
    }

    /**
     * Called when a user explicitly joins (e.g., from MessagingController).
     */
    public void setUserOnline(String username, String currentIp) {
        userHeartbeats.put(username, Instant.now()); // Start tracking heartbeat on connect
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus(UserStatus.ONLINE);
            user.setCurrentIp(currentIp);
            userRepository.save(user);
        }
    }

    /**
     * Called when a user is confirmed to be offline (after grace period or explicit logout).
     */
    public void setUserOffline(String username) {
        userHeartbeats.remove(username); // Stop tracking heartbeat
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus(UserStatus.OFFLINE);
            user.setCurrentIp(null);
            userRepository.save(user);
        }
    }

    public Map<String, Instant> getHeartbeatMap() {
        return userHeartbeats;
    }

    public void broadcastUserList() {
        if (appConfigService.getUserListVisibility() == AppConfigService.Visibility.PUBLIC) {
            messagingTemplate.convertAndSend("/topic/users", getAdminUserList());
        }
    }
    // ✅ ADD THIS METHOD
    public void removeUserFromHeartbeat(String username) {
        userHeartbeats.remove(username);
    }

    public List<UserDto> getAdminUserList() {
        return userRepository.findAll().stream()
                .map(user -> {
                    String statusString = (user.getStatus() != null) ? user.getStatus().name() : "OFFLINE";
                    return new UserDto(
                            user.getId(),
                            user.getUsername(),
                            user.getRole(),
                            statusString,
                            user.getCurrentIp()
                    );
                })
                .collect(Collectors.toList());
    }
}