package com.lanmessenger.model;

/**
 * Represents a chat message that is sent over the WebSocket connection.
 * This object is converted to/from JSON for transport.
 */
public class ChatMessage {

    private String content;
    private String sender;
    private String recipient; // Used for private messages
    private MessageType type;

    /**
     * Defines the type of message being sent.
     */
    public enum MessageType {
        CHAT,   // A regular public group message
        JOIN,   // A user joining the chat
        LEAVE,  // A user leaving the chat
        PRIVATE // A private, one-to-one message
    }

    // --- Getters and Setters ---
    // These are required for the framework to serialize/deserialize the object.

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
