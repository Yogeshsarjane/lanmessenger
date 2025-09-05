package com.lanmessenger.model;

// A record is a concise way to create an immutable data carrier
public record FileNotification(String fileId, String fileName, String sender) {
}