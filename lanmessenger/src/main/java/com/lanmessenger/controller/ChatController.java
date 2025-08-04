package com.lanmessenger.controller;

import com.lanmessenger.model.ChatMessage;
import com.lanmessenger.model.ChatMessageLog;
import com.lanmessenger.repository.ChatMessageLogRepository;
import com.lanmessenger.service.UserService;
import com.lanmessenger.service.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageLogRepository chatLogRepository;

    /**
     * --- THIS METHOD HAS BEEN FIXED ---
     * It no longer uses @SendTo. Instead, it manually broadcasts the message
     * using the messagingTemplate, which is more reliable.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        System.out.println(">>> Received public message: [" + chatMessage.getSender() + ": " + chatMessage.getContent() + "]");

        saveChatMessage(chatMessage, "group");

        // Broadcast the message to everyone subscribed to /topic/public
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        WebSocketEventListener.addUser(chatMessage.getSender());
        userService.broadcastUserList();

        // Also broadcast the JOIN message to the public topic
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
        ChatMessageLog log = new ChatMessageLog();
        log.setSender(chatMessage.getSender());
        log.setRecipient(recipient);
        log.setContent(chatMessage.getContent());
        log.setTimestamp(LocalDateTime.now());
        chatLogRepository.save(log);
    }
}
