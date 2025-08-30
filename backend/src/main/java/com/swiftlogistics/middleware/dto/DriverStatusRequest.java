package com.swiftlogistics.middleware.dto;

import com.swiftlogistics.middleware.model.Driver;

/**
 * DTO for driver status update requests
 */
public class DriverStatusRequest {
    
    private Driver.DriverStatus status;
    private String notes;
    
    // Constructors
    public DriverStatusRequest() {}
    
    public DriverStatusRequest(Driver.DriverStatus status) {
        this.status = status;
    }
    
    public DriverStatusRequest(Driver.DriverStatus status, String notes) {
        this.status = status;
        this.notes = notes;
    }
    
    // Getters and Setters
    public Driver.DriverStatus getStatus() { return status; }
    public void setStatus(Driver.DriverStatus status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
