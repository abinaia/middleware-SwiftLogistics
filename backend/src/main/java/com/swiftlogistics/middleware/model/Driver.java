package com.swiftlogistics.middleware.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "drivers")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "driver_id", nullable = false, unique = true)
    private String driverId;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "license_number")
    private String licenseNumber;
    
    @Column(name = "vehicle_number")
    private String vehicleNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DriverStatus status = DriverStatus.INACTIVE;
    
    @Column(name = "shift_start_time")
    private LocalDateTime shiftStartTime;
    
    @Column(name = "shift_end_time")
    private LocalDateTime shiftEndTime;
    
    @Column(name = "total_distance_today")
    private Double totalDistanceToday = 0.0;
    
    @Column(name = "completed_deliveries_today")
    private Integer completedDeliveriesToday = 0;
    
    @Column(name = "average_rating")
    private Double averageRating = 0.0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum for driver status
    public enum DriverStatus {
        ACTIVE,
        INACTIVE,
        ON_BREAK,
        OFF_DUTY,
        UNAVAILABLE
    }
    
    // Constructors
    public Driver() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Driver(String driverId, String name, String email) {
        this();
        this.driverId = driverId;
        this.name = name;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getShiftStartTime() { return shiftStartTime; }
    public void setShiftStartTime(LocalDateTime shiftStartTime) { this.shiftStartTime = shiftStartTime; }
    
    public LocalDateTime getShiftEndTime() { return shiftEndTime; }
    public void setShiftEndTime(LocalDateTime shiftEndTime) { this.shiftEndTime = shiftEndTime; }
    
    public Double getTotalDistanceToday() { return totalDistanceToday; }
    public void setTotalDistanceToday(Double totalDistanceToday) { this.totalDistanceToday = totalDistanceToday; }
    
    public Integer getCompletedDeliveriesToday() { return completedDeliveriesToday; }
    public void setCompletedDeliveriesToday(Integer completedDeliveriesToday) { this.completedDeliveriesToday = completedDeliveriesToday; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business methods
    public void startShift() {
        this.status = DriverStatus.ACTIVE;
        this.shiftStartTime = LocalDateTime.now();
        this.totalDistanceToday = 0.0;
        this.completedDeliveriesToday = 0;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void endShift() {
        this.status = DriverStatus.OFF_DUTY;
        this.shiftEndTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void incrementCompletedDeliveries() {
        this.completedDeliveriesToday++;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void addDistance(Double distance) {
        this.totalDistanceToday += distance;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", driverId='" + driverId + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
