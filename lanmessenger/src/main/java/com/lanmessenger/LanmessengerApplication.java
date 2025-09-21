package com.lanmessenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // <-- ADD THIS ANNOTATION
public class LanmessengerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LanmessengerApplication.class, args);
	}
}
