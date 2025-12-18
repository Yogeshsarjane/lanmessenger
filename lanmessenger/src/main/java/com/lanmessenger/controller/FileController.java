package com.lanmessenger.controller;

import com.lanmessenger.model.FileLog;
import com.lanmessenger.model.MessageStatus;
import com.lanmessenger.repository.FileLogRepository;
import com.lanmessenger.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal; // ✅ Added
import java.time.LocalDateTime;
import java.util.List;          // ✅ Added

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileLogRepository fileLogRepository;

    public FileController(FileStorageService fileStorageService,
                          SimpMessagingTemplate messagingTemplate,
                          FileLogRepository fileLogRepository) {
        this.fileStorageService = fileStorageService;
        this.messagingTemplate = messagingTemplate;
        this.fileLogRepository = fileLogRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileLog> uploadFile(@RequestParam("file") MultipartFile file,
                                              @RequestParam("sender") String sender,
                                              @RequestParam("recipient") String recipient) {

        // 1. Store the file on the server
        String storedFilename = fileStorageService.storeFile(file);

        // 2. Create the Database Entry
        FileLog fileLog = new FileLog(); // Unified variable name
        fileLog.setSender(sender);
        fileLog.setRecipient(recipient);
        fileLog.setOriginalFilename(file.getOriginalFilename());
        fileLog.setStoredFilename(storedFilename);
        fileLog.setTimestamp(LocalDateTime.now());

        // 3. Set status to SENT (Pending) initially
        fileLog.setStatus(MessageStatus.SENT);

        // 4. Save to Database ONCE
        FileLog savedLog = fileLogRepository.save(fileLog);

        // 5. Notify the recipient via WebSocket
        // We send the full 'savedLog' object so the frontend gets the ID and Status
        messagingTemplate.convertAndSendToUser(
                recipient, "/queue/files", savedLog);

        log.info("File uploaded and notification sent. ID: {}", savedLog.getId());

        // 6. Return the saved log object (Fixed return type mismatch)
        return ResponseEntity.ok(savedLog);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        // Extract original name logic (or retrieve from DB if preferred)
        String originalFilename = fileId.contains("_")
                ? fileId.substring(fileId.indexOf("_") + 1)
                : fileId;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FileLog>> getPendingFiles(Principal principal) {
        String username = principal.getName();

        // 1. Find undelivered files
        List<FileLog> pendingFiles = fileLogRepository.findByRecipientAndStatus(username, MessageStatus.SENT);

        // 2. Mark them as DELIVERED so they don't load again next time
        for (FileLog file : pendingFiles) {
            file.setStatus(MessageStatus.DELIVERED);
        }
        fileLogRepository.saveAll(pendingFiles);

        return ResponseEntity.ok(pendingFiles);
    }
}