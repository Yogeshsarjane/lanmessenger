package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.model.UserStatus; // <-- Make sure this is imported
import com.lanmessenger.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class UserInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            System.out.println("No users found in DB. Initializing from CSV.");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new ClassPathResource("initial-users.csv").getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",", -1);

                    if (data.length >= 3) {
                        User user = new User();
                        user.setUsername(data[0].trim());
                        user.setPassword(passwordEncoder.encode(data[1].trim()));
                        user.setRole(data[2].trim());

                        // ✅ Use the UserStatus.OFFLINE enum, not the string "Offline"
                        user.setStatus(UserStatus.OFFLINE);

                        if (data.length > 3 && !data[3].trim().isEmpty()) {
                            user.setAllowedIp(data[3].trim());
                        }

                        userRepository.save(user);
                    }
                }
                System.out.println("Finished initializing users.");
            } catch (Exception e) {
                System.err.println("Error initializing users from CSV: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Database already contains users. Skipping initialization.");
        }
    }
}