package com.lanmessenger.model;

public class UserDto {
    private Long id;
    private String username;
    private String role;
    private String status;
    private String ipAddress;

    public UserDto(Long id, String username, String role, String status, String ipAddress) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.status = status;
        this.ipAddress = ipAddress;
    }

    // --- GETTERS ---

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    // --- SETTERS ---

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}