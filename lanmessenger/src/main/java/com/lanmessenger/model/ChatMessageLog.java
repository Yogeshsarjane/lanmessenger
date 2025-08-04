package com.lanmessenger.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ChatMessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    // This will be "group" for public messages, or a username for private ones.
    private String recipient;

    @Column(columnDefinition = "TEXT") // Allows for longer messages
    private String content;

    private LocalDateTime timestamp;

    // Getters and Setters for all fields...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
