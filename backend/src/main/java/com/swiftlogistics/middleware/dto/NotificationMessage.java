package com.swiftlogistics.middleware.dto;

import java.time.LocalDateTime;

/**
 * DTO for notification messages
 */
public class NotificationMessage {
    private Long id;
    private Long clientId;
    private String title;
    private String message;
    private String type; // SUCCESS, INFO, WARNING, ERROR
    private LocalDateTime timestamp;
    private boolean read;
    private String action; // Optional action URL or identifier
    private String icon; // Optional icon name

    // Constructors
    public NotificationMessage() {
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }

    public NotificationMessage(Long clientId, String title, String message, String type) {
        this();
        this.clientId = clientId;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "NotificationMessage{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
