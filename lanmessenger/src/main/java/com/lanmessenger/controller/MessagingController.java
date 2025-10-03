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
import java.time.format.DateTimeFormatter;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

@Controller
public class MessagingController {

    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageLogRepository chatLogRepository;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");


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
        // Save the message and get the log entry with the timestamp
        ChatMessageLog log = saveChatMessage(chatMessage, "group");

        // ✅ Add the formatted timestamp to the message object before sending
        chatMessage.setTimestamp(log.getTimestamp().format(timeFormatter));

        messagingTemplate.convertAndSend("/topic/public", chatMessage);
    }

    // In MessagingController.java

    @MessageMapping("/chat.privateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {
        // Save the message to the log, which adds the server timestamp
        ChatMessageLog log = saveChatMessage(chatMessage, chatMessage.getRecipient());

        // Add the formatted timestamp to the message object before sending
        if (log != null && log.getTimestamp() != null) {
            chatMessage.setTimestamp(log.getTimestamp().format(timeFormatter));
        }

        // 1. Send the message to the recipient's private queue
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipient(),
                "/queue/private",
                chatMessage
        );

        // 2. ✅ ALSO SEND THE MESSAGE BACK to the sender's private queue
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSender(),
                "/queue/private",
                chatMessage
        );
    }
    // In MessagingController.java

// ... constructor and other methods ...

    // ✅ NEW: Client sends this every 20 seconds
    @MessageMapping("/heartbeat.ping")
    public void handlePing(@Payload ChatMessage message) {
        userService.updateUserHeartbeat(message.getSender());
    }

    // ✅ NEW: Client sends this right before logging out
    @MessageMapping("/heartbeat.logout")
    public void handleLogout(@Payload ChatMessage message) {
        String username = message.getSender();
        userService.setUserOffline(username);
        userService.removeUserFromHeartbeat(username);
        userService.broadcastUserList();

        // Also send the LEAVE message to the group chat
        ChatMessage leaveMessage = new ChatMessage();
        leaveMessage.setType(ChatMessage.MessageType.LEAVE);
        leaveMessage.setSender(username);
        messagingTemplate.convertAndSend("/topic/public", leaveMessage);
    }
    // IMPORTANT: Change this method to return the saved log object
    private ChatMessageLog saveChatMessage(ChatMessage chatMessage, String recipient) {
        if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            return null;
        }
        ChatMessageLog log = new ChatMessageLog();
        log.setSender(chatMessage.getSender());
        log.setRecipient(recipient);
        log.setContent(chatMessage.getContent());
        log.setTimestamp(LocalDateTime.now());
        return chatLogRepository.save(log); // ✅ Return the saved object
    }
}