package com.lanmessenger.repository;

import com.lanmessenger.model.ChatMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface ChatMessageLogRepository extends JpaRepository<ChatMessageLog, Long> {

    // This is your existing method for user-specific history - KEEP IT.
    @Query("SELECT c FROM ChatMessageLog c WHERE c.recipient = 'group' OR c.sender = :username OR c.recipient = :username ORDER BY c.timestamp ASC")
    List<ChatMessageLog> findChatHistoryForUser(@Param("username") String username);

    // ✅ ADD THIS NEW METHOD
    /**
     * Finds the 50 most recent messages sent to a specific recipient (e.g., "group"),
     * ordered by the newest timestamp first.
     * @param recipient The recipient to search for ("group").
     * @return A list of the 50 most recent chat logs.
     */
    List<ChatMessageLog> findFirst50ByRecipientOrderByTimestampDesc(String recipient);

    // ✅ ADD THIS NEW METHOD
    /**
     * Finds the most recent messages between two users (user1 and user2).
     * It looks for messages where (sender=user1 AND recipient=user2) OR (sender=user2 AND recipient=user1).
     * The results are ordered by the newest timestamp first.
     * @param user1 The username of the first user.
     * @param user2 The username of the second user.
     * @param pageable A Pageable object to limit the number of results (e.g., to 20).
     * @return A list of the most recent chat logs between the two users.
     */
    @Query("SELECT c FROM ChatMessageLog c WHERE (c.sender = :user1 AND c.recipient = :user2) OR (c.sender = :user2 AND c.recipient = :user1) ORDER BY c.timestamp DESC")
    List<ChatMessageLog> findPrivateChatHistory(@Param("user1") String user1, @Param("user2") String user2, Pageable pageable);

}