package com.lanmessenger.repository;

import com.lanmessenger.model.FileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.lanmessenger.model.MessageStatus;

import java.util.List;

@Repository
public interface FileLogRepository extends JpaRepository<FileLog, Long> {

    /**
     * Finds all file logs where the given user was either the sender or the recipient.
     * @param username The username to search for.
     * @return A list of relevant file logs.
     */
    @Query("SELECT f FROM FileLog f WHERE f.sender = :username OR f.recipient = :username ORDER BY f.timestamp ASC")
    List<FileLog> findFileHistoryForUser(@Param("username") String username);

    // ✅ Find files sent to a specific user that haven't been delivered yet
    List<FileLog> findByRecipientAndStatus(String recipient, MessageStatus status);

}
