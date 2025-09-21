

package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.lanmessenger.model.UserStatus; // <-- IMPORT

import java.util.List;
import java.util.stream.Collectors;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AppConfigService appConfigService; // ✅ 1. Declare the field
    private final Map<String, Instant> unstableUsers = new ConcurrentHashMap<>();


    // ✅ 2. Add AppConfigService to the constructor
    public UserService(UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate,
                       AppConfigService appConfigService) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.appConfigService = appConfigService;
    }

    public void setUserOnline(String username, String currentIp) {
        unstableUsers.remove(username); // Remove from unstable list if they reconnect
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus(UserStatus.ONLINE);
            user.setCurrentIp(currentIp);
            userRepository.save(user);
        }
    }

    // ✅ NEW METHOD
    public void setUserUnstable(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getStatus() == UserStatus.ONLINE) {
            user.setStatus(UserStatus.UNSTABLE);
            userRepository.save(user);
            unstableUsers.put(username, Instant.now());
        }
    }

    public void setUserOffline(String username) {
        unstableUsers.remove(username); // Clean up the map
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus(UserStatus.OFFLINE);
            user.setCurrentIp(null);
            userRepository.save(user);
        }
    }

    // This method will be called by our scheduled task
    public Map<String, Instant> getUnstableUsers() {
        return unstableUsers;
    }

    public void broadcastUserList() {
        // ✅ 3. Now this line will work correctly
        if (appConfigService.getUserListVisibility() == AppConfigService.Visibility.PUBLIC) {
            messagingTemplate.convertAndSend("/topic/users", getAdminUserList());
        }
    }
    // In UserService.java
    public List<UserDto> getAdminUserList() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // Handle case where status might be null for older records
                    String statusString = (user.getStatus() != null) ? user.getStatus().name() : "OFFLINE";

                    return new UserDto(
                            user.getId(),
                            user.getUsername(),
                            user.getRole(),
                            statusString, // ✅ Use the converted string
                            user.getCurrentIp()
                    );
                })
                .collect(Collectors.toList());
    }
}