package com.lanmessenger.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Fault {

    public enum Category {
        CHAT_ISSUE, FILE_SHARING, UI_BUG, CONNECTION, OTHER
    }

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Priority priority;

    @Column(length = 2000) // Allow for a long description
    private String description;

    private String reporterUsername;
    private LocalDateTime submissionTimestamp;
    private String screenshotFilename; // To store the name of the saved file

    // --- GETTERS AND SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public LocalDateTime getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public void setSubmissionTimestamp(LocalDateTime submissionTimestamp) {
        this.submissionTimestamp = submissionTimestamp;
    }

    public String getScreenshotFilename() {
        return screenshotFilename;
    }

    public void setScreenshotFilename(String screenshotFilename) {
        this.screenshotFilename = screenshotFilename;
    }
}