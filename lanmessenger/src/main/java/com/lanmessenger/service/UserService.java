package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.model.UserDto;
import com.lanmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppConfigService appConfigService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * This is now the single, intelligent broadcast method.
     */
    public void broadcastUserList() {
        // Get the full list of users with their current status.
        List<UserDto> fullUserList = getAllUsersWithStatus();

        // 1. Admins ALWAYS get the full list on their private topic.
        messagingTemplate.convertAndSend("/topic/admin/users", fullUserList);

        // 2. Regular users get a list based on the visibility setting.
        if (appConfigService.getUserListVisibility() == AppConfigService.Visibility.PUBLIC) {
            messagingTemplate.convertAndSend("/topic/users", fullUserList);
        } else {
            messagingTemplate.convertAndSend("/topic/users", Collections.emptyList());
        }
    }

    /**
     * Gets the full, unfiltered user list for the admin's initial page load.
     */
    public List<UserDto> getAdminUserList() {
        return getAllUsersWithStatus();
    }

    private List<UserDto> getAllUsersWithStatus() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserDto convertToDto(User user) {
        String status = WebSocketEventListener.isUserActive(user.getUsername()) ? "Online" : "Offline";
        String ipAddress = "N/A"; // Placeholder
        return new UserDto(user.getId(), user.getUsername(), user.getRole(), status, ipAddress);
    }
}
