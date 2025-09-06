package com.lanmessenger.service;

import com.lanmessenger.model.ChatMessage;
import com.lanmessenger.model.User;
import com.lanmessenger.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Objects;

@Component
@Slf4j
public class WebSocketEventListener {

    private static final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        String username = Objects.requireNonNull(headers.getUser()).getName();

        InetSocketAddress address = (InetSocketAddress) headers.getSessionAttributes().get("remoteAddress");
        if (address != null) {
            String ipAddress = address.getAddress().getHostAddress();
            log.info("User {} connected with IP: {}", username, ipAddress);

            // ✅ This is the corrected logic for JPA
            // 1. Find the user from the database
            User user = userRepository.findByUsername(username);
            if (user != null) {
                // 2. Set the current IP on the user object
                user.setCurrentIp(ipAddress);
                // 3. Save the updated user object back to the database
                userRepository.save(user);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {
            String username = (String) sessionAttributes.get("username");
            if (username != null) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setType(ChatMessage.MessageType.LEAVE);
                chatMessage.setSender(username);
                activeUsers.remove(username);
                messagingTemplate.convertAndSend("/topic/public", chatMessage);

                // Use the service to broadcast the list according to visibility rules.
                userService.broadcastUserList();
            }
        }
    }

    public static void addUser(String username) {
        activeUsers.add(username);
    }

    public static boolean isUserActive(String username) {
        return activeUsers.contains(username);
    }
}
