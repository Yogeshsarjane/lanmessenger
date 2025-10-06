package com.lanmessenger.controller;

import com.lanmessenger.model.Fault;
import com.lanmessenger.repository.FaultRepository;
import com.lanmessenger.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/faults")
public class FaultController {

    private final FaultRepository faultRepository;
    private final FileStorageService fileStorageService;

    public FaultController(FaultRepository faultRepository, FileStorageService fileStorageService) {
        this.faultRepository = faultRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<Void> submitFault(
            Principal principal,
            @RequestParam String subject,
            @RequestParam Fault.Category category,
            @RequestParam Fault.Priority priority,
            @RequestParam String description,
            @RequestParam(required = false) MultipartFile screenshot) {

        Fault fault = new Fault();
        fault.setReporterUsername(principal.getName());
        fault.setSubject(subject);
        fault.setCategory(category);
        fault.setPriority(priority);
        fault.setDescription(description);
        fault.setSubmissionTimestamp(LocalDateTime.now());

        if (screenshot != null && !screenshot.isEmpty()) {
            String filename = fileStorageService.storeFile(screenshot);
            fault.setScreenshotFilename(filename);
        }

        faultRepository.save(fault);
        return ResponseEntity.ok().build();
    }
}