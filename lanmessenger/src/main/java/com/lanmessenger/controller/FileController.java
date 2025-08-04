package com.lanmessenger.controller;

import com.lanmessenger.model.FileLog;
import com.lanmessenger.repository.FileLogRepository;
import com.lanmessenger.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileLogRepository fileLogRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("recipient") String recipient,
                                             @AuthenticationPrincipal UserDetails currentUser) {
        // Store the physical file
        String storedFilename = fileStorageService.storeFile(file);

        // Create a log entry in the database
        FileLog fileLog = new FileLog();
        fileLog.setOriginalFilename(file.getOriginalFilename());
        fileLog.setStoredFilename(storedFilename);
        fileLog.setSender(currentUser.getUsername());
        fileLog.setRecipient(recipient);
        fileLog.setTimestamp(LocalDateTime.now());
        fileLogRepository.save(fileLog);

        // Here we will add WebSocket notification logic later
        // For now, we just confirm the upload was successful.

        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(filename);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // Fallback to the default content type if type could not be determined
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
