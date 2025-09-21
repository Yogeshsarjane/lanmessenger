package com.lanmessenger.repository;

import com.lanmessenger.model.ChatMessageLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageLogRepository extends JpaRepository<ChatMessageLog, Long> {

    // For the Admin's user-specific history panel
    @Query("SELECT c FROM ChatMessageLog c WHERE c.recipient = 'group' OR c.sender = :username OR c.recipient = :username ORDER BY c.timestamp ASC")
    List<ChatMessageLog> findChatHistoryForUser(@Param("username") String username);

    // Flexible method for fetching group chat history (used by both admin and user)
    List<ChatMessageLog> findByRecipientOrderByTimestampDesc(String recipient, Pageable pageable);

    // For fetching private chat history between two users
    @Query("SELECT c FROM ChatMessageLog c WHERE (c.sender = :user1 AND c.recipient = :user2) OR (c.sender = :user2 AND c.recipient = :user1) ORDER BY c.timestamp DESC")
    List<ChatMessageLog> findPrivateChatHistory(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);
}