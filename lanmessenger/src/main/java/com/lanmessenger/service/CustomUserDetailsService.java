package com.lanmessenger.service;

import com.lanmessenger.model.CustomUserDetails; // ✅ IMPORT our custom class
import com.lanmessenger.model.User;
import com.lanmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Step 1: Find your user from the repository (this part is perfect)
        User user = userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        // ✅ Step 2: Create our CUSTOM UserDetails object instead of the standard one
        // We now pass in user.getAllowedIp() as the 4th argument.
        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())),
                user.getAllowedIp() // <-- This is the only new part
        );
    }
}