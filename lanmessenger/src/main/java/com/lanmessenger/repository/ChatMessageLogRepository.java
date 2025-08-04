package com.lanmessenger.repository;

import com.lanmessenger.model.ChatMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageLogRepository extends JpaRepository<ChatMessageLog, Long> {

    /**
     * Finds all chat messages relevant to a specific user.
     * This includes:
     * 1. All public group messages.
     * 2. All private messages sent BY this user.
     * 3. All private messages sent TO this user.
     * @param username The username to search for.
     * @return A list of relevant chat logs.
     */
    @Query("SELECT c FROM ChatMessageLog c WHERE c.recipient = 'group' OR c.sender = :username OR c.recipient = :username ORDER BY c.timestamp ASC")
    List<ChatMessageLog> findChatHistoryForUser(@Param("username") String username);

}
