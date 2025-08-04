package com.lanmessenger.service;

import com.lanmessenger.model.User;
import com.lanmessenger.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

@Component
public class UserInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only run this initializer if the user table is empty
        if (userRepository.count() == 0) {
            System.out.println("No users found in DB. Initializing from CSV.");
            try {
                // Load the CSV file from the resources folder
                ClassPathResource resource = new ClassPathResource("initial-users.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

                String line;
                // Read the file line by line
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length == 3) {
                        User user = new User();
                        user.setUsername(data[0].trim());
                        // IMPORTANT: Hash the plain-text password before saving
                        user.setPassword(passwordEncoder.encode(data[1].trim()));
                        user.setRole(data[2].trim());
                        userRepository.save(user);
                    }
                }
                reader.close();
                System.out.println("Finished initializing users.");
            } catch (Exception e) {
                System.err.println("Error initializing users from CSV: " + e.getMessage());
            }
        } else {
            System.out.println("Database already contains users. Skipping initialization.");
        }
    }
}