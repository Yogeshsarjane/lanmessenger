package com.lanmessenger.model; // Or your security/model package

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUserDetails extends User {

    private final String allowedIp;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, String allowedIp) {
        super(username, password, authorities);
        this.allowedIp = allowedIp;
    }

    public String getAllowedIp() {
        return allowedIp;
    }
}