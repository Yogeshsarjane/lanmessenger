package com.lanmessenger.controller;

import com.lanmessenger.model.ChatMessage;
import com.lanmessenger.model.ChatMessageLog;
import com.lanmessenger.repository.ChatMessageLogRepository;
import com.lanmessenger.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

@Controller
public class MessagingController {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageLogRepository chatLogRepository;

    public MessagingController(UserService userService,
                               SimpMessagingTemplate messagingTemplate,
                               ChatMessageLogRepository chatLogRepository) {
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.chatLogRepository = chatLogRepository;
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String username = chatMessage.getSender();
        // 1. Link username to the session (CRITICAL for disconnects)
        headerAccessor.getSessionAttributes().put("username", username);

        // 2. Get IP and tell the UserService the user is ONLINE
        InetSocketAddress address = (InetSocketAddress) headerAccessor.getSessionAttributes().get("remoteAddress");
        String ipAddress = (address != null) ? address.getAddress().getHostAddress() : null;
        userService.setUserOnline(username, ipAddress);

        // 3. Broadcast the updated user list
        userService.broadcastUserList();

        // 4. Send the JOIN message to the public chat
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        saveChatMessage(chatMessage, "group");
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        saveChatMessage(chatMessage, chatMessage.getRecipient());
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/private",
                chatMessage
        );
    }

    private void saveChatMessage(ChatMessage chatMessage, String recipient) {
        if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            return; // Don't save empty or JOIN/LEAVE messages
        }
        ChatMessageLog log = new ChatMessageLog();
        log.setSender(chatMessage.getSender());
        log.setRecipient(recipient);
        log.setContent(chatMessage.getContent());
        log.setTimestamp(LocalDateTime.now());
        chatLogRepository.save(log);
    }
}