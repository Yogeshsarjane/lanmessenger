package com.lanmessenger.controller;

import com.lanmessenger.model.FileNotification;
import com.lanmessenger.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/files")
@Slf4j
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // For sending WebSocket messages

    /**
     * Handles the file upload, stores the file, and notifies the recipient.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("sender") String sender,
                                             @RequestParam("recipient") String recipient) {
        // 1. Store the file on the server
        String fileId = fileStorageService.storeFile(file);

        // 2. Create a notification payload
        FileNotification notification = new FileNotification(fileId, file.getOriginalFilename(), sender);

        // 3. Send a private WebSocket message to the recipient
        // The message is sent to the user's personal queue, e.g., /user/john/queue/files
        messagingTemplate.convertAndSendToUser(recipient, "/queue/files", notification);

        // ✅ THIS IS THE CRUCIAL LOG LINE
        log.info("--- Sending file notification to: {} ---", recipient);

        // This is also a good place to save the file transfer details to a database.

        return ResponseEntity.ok("File uploaded successfully. Notification sent to " + recipient);

    }

    /**
     * Handles the file download request.
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        Resource resource = fileStorageService.loadFileAsResource(fileId);

        // Extract original filename (everything after the first '_')
        String originalFilename = fileId.substring(fileId.indexOf("_") + 1);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFilename + "\"")
                .body(resource);
    }
}