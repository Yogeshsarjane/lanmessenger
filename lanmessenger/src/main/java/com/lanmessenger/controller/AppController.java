package com.lanmessenger.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // This method now directs users to the correct dashboard based on their role
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        // Check if the user has the 'ADMIN' role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "admin_dashboard"; // The view for admins
        } else {
            return "user_dashboard";  // The view for regular users
        }
    }
}