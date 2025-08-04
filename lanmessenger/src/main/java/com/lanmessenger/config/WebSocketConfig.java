package com.lanmessenger.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for messages sent from clients to the server
        registry.setApplicationDestinationPrefixes("/app");
        // Prefixes for topics the server broadcasts messages to
        // /topic is for public broadcasts (like group chat)
        // /user is for user-specific messages (like private chat)
        registry.enableSimpleBroker("/topic", "/user");
        // This configures the destination for user-specific messages
        registry.setUserDestinationPrefix("/user");
    }
}
