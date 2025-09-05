package com.lanmessenger.model;

import jakarta.persistence.*;

@Entity
// This tells JPA to use the table name "user" and to quote it because it's a reserved keyword.
// If you renamed your table to "users", you should change this to @Table(name = "users")
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This is the crucial line that was missing.
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    private String allowedIp; // Make sure this exists

    // Make sure you have getters for all fields
    public String getAllowedIp() {
        return allowedIp;
    }

    // JPA requires a no-argument constructor
    public User() {
    }

    // --- Getters and Setters ---

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

    public void setAllowedIp(String allowedIp) {
        this.allowedIp = allowedIp;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
