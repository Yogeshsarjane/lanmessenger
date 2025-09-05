package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder; // Changed from BCryptPasswordEncoder for flexibility
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class UserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Use the interface, not the implementation

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("No users found in DB. Initializing from CSV.");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("initial-users.csv").getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    // This logic now correctly handles lines with 3 or 4 columns
                    String[] data = line.split(",", -1); // Use -1 to keep trailing empty columns

                    if (data.length >= 3) { // Process any line with at least 3 columns
                        User user = new User();
                        user.setUsername(data[0].trim());
                        user.setPassword(passwordEncoder.encode(data[1].trim()));
                        user.setRole(data[2].trim());

                        // Check if the 4th column (allowedIp) exists and is not empty
                        if (data.length > 3 && !data[3].trim().isEmpty()) {
                            user.setAllowedIp(data[3].trim());
                        }

                        userRepository.save(user);
                    }
                }
                System.out.println("Finished initializing users.");
            } catch (Exception e) {
                System.err.println("Error initializing users from CSV: " + e.getMessage());
                e.printStackTrace(); // Print full stack trace for better debugging
            }
        } else {
            System.out.println("Database already contains users. Skipping initialization.");
        }
    }
}