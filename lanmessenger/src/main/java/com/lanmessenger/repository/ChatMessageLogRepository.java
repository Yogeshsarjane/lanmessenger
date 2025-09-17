package com.lanmessenger.repository;

import com.lanmessenger.model.ChatMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

}