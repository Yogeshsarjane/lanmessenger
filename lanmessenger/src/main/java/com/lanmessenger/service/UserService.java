

package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AppConfigService appConfigService; // ✅ 1. Declare the field

    // ✅ 2. Add AppConfigService to the constructor
    public UserService(UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate,
                       AppConfigService appConfigService) {
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.appConfigService = appConfigService;
    }

    public void setUserOnline(String username, String currentIp) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus("Online");
            user.setCurrentIp(currentIp);
            userRepository.save(user);
        }
    }

    public void setUserOffline(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setStatus("Offline");
            user.setCurrentIp(null);
            userRepository.save(user);
        }
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
                .map(user -> new UserDto(
                        // These 5 arguments now perfectly match your UserDto constructor
                        user.getId(),           // 1. id
                        user.getUsername(),     // 2. username
                        user.getRole(),         // 3. role
                        user.getStatus(),       // 4. status
                        user.getCurrentIp()     // 5. ipAddress (we use the user's current IP for this)
                ))
                .collect(Collectors.toList());
    }
}