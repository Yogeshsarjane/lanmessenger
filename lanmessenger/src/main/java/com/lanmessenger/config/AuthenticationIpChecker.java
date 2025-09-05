package com.lanmessenger.config; // Or your security package

import com.lanmessenger.model.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import lombok.extern.slf4j.Slf4j; // <-- Add this import
@Component
@Slf4j // <-- Add this annotation
public class AuthenticationIpChecker implements ApplicationListener<AuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Get the user who just logged in
        Object principal = event.getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            String allowedIp = userDetails.getAllowedIp();

            // If an allowed IP is specified for this user, we must check it
            if (allowedIp != null && !allowedIp.isEmpty()) {
                // Get the actual IP from the current web request
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                String remoteIp = request.getRemoteAddr();
                log.info("IP CHECK - Allowed: |{}|, Actual: |{}|", allowedIp, remoteIp);
                // If the IPs do not match, we reject the login
                if (!allowedIp.equals(remoteIp)) {
                    // This exception will be caught by Spring Security and will stop the login.
                    throw new BadCredentialsException("Invalid IP Address");
                }
            }
        }
    }
}