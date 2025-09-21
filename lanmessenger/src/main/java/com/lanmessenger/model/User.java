package com.lanmessenger.model;

import jakarta.persistence.*;

// 1. IMPORT the new enum
import com.lanmessenger.model.UserStatus;

@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "allowed_ip") // Added this annotation for consistency
    private String allowedIp;

    @Column(name = "current_ip")
    private String currentIp;

    // 2. CHANGE the type of the 'status' field from String to UserStatus
    @Enumerated(EnumType.STRING) // 3. ADD this annotation to tell the database how to store it
    @Column(name = "status")
    private UserStatus status;

    public User() {
    }

    // --- Getters and Setters ---

    // 4. UPDATE the getter to return UserStatus
    public UserStatus getStatus() {
        return status;
    }

    // 5. UPDATE the setter to accept UserStatus
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    // --- Other getters and setters remain the same ---

    public String getAllowedIp() {
        return allowedIp;
    }

    public void setAllowedIp(String allowedIp) {
        this.allowedIp = allowedIp;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}