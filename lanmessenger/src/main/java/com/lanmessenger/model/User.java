package com.lanmessenger.model;

import jakarta.persistence.*;

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

    // Good practice to specify column name
    private String allowedIp;

    @Column(name = "current_ip") // Add column for current IP
    private String currentIp; // ✅ Make sure this field exists

    // JPA requires a no-argument constructor
    public User() {
    }

    // --- Getters and Setters ---

    public String getAllowedIp() {
        return allowedIp;
    }

    public void setAllowedIp(String allowedIp) {
        this.allowedIp = allowedIp;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    // ✅ ADD THIS SETTER METHOD
    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }

    // ... other getters and setters for id, username, password, role ...

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