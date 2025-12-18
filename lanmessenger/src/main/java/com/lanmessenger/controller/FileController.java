package com.lanmessenger.controller;

import com.lanmessenger.model.FileLog; // <-- IMPORT
import com.lanmessenger.model.FileNotification;
import com.lanmessenger.repository.FileLogRepository; // <-- IMPORT
import com.lanmessenger.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime; // <-- IMPORT

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    // --- Declare all dependencies as final fields ---
    private final FileStorageService fileStorageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileLogRepository fileLogRepository; // <-- ADD THIS

    // --- Use one constructor to inject everything (Best Practice) ---
    public FileController(FileStorageService fileStorageService,
                          SimpMessagingTemplate messagingTemplate,
                          FileLogRepository fileLogRepository) { // <-- ADD THIS
        this.fileStorageService = fileStorageService;
        this.messagingTemplate = messagingTemplate;
        this.fileLogRepository = fileLogRepository; // <-- ADD THIS
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("sender") String sender,
                                             @RequestParam("recipient") String recipient) {

        // 1. Store the file on the server
        String storedFilename = fileStorageService.storeFile(file);

        // 2. Create and send the WebSocket notification
        FileNotification notification = new FileNotification(storedFilename, file.getOriginalFilename(), sender);
        messagingTemplate.convertAndSendToUser(recipient, "/queue/files", notification);
        log.info("Sent file notification for {} to recipient: {}", file.getOriginalFilename(), recipient);

        // ✅ 3. CREATE AND SAVE THE FILE LOG
        FileLog logEntry = new FileLog();
        logEntry.setSender(sender);
        logEntry.setRecipient(recipient);
        logEntry.setOriginalFilename(file.getOriginalFilename());
        logEntry.setStoredFilename(storedFilename); // Save the unique name
        logEntry.setTimestamp(LocalDateTime.now());
        fileLogRepository.save(logEntry);

        return ResponseEntity.ok("File uploaded successfully.");
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        Resource resource = fileStorageService.loadFileAsResource(fileId);
        String originalFilename = fileId.substring(fileId.indexOf("_") + 1);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }
}