package com.swiftlogistics.middleware.dto;

import com.swiftlogistics.middleware.model.Driver;
import java.time.LocalDateTime;

/**
 * DTO for driver dashboard data
 */
public class DriverDashboardResponse {
    
    private String driverId;
    private String name;
    private String vehicleNumber;
    private Driver.DriverStatus status;
    private LocalDateTime shiftStartTime;
    private Integer activeDeliveries;
    private Integer completedToday;
    private Double totalDistanceToday;
    private Double averageRating;
    private CurrentRouteInfo currentRoute;
    private ShiftInfo shiftInfo;
    
    // Constructors
    public DriverDashboardResponse() {}
    
    public DriverDashboardResponse(Driver driver) {
        this.driverId = driver.getDriverId();
        this.name = driver.getName();
        this.vehicleNumber = driver.getVehicleNumber();
        this.status = driver.getStatus();
        this.shiftStartTime = driver.getShiftStartTime();
        this.completedToday = driver.getCompletedDeliveriesToday();
        this.totalDistanceToday = driver.getTotalDistanceToday();
        this.averageRating = driver.getAverageRating();
        
        // Initialize shift info
        this.shiftInfo = new ShiftInfo();
        this.shiftInfo.setStartTime(driver.getShiftStartTime());
        this.shiftInfo.setEndTime(driver.getShiftEndTime());
        this.shiftInfo.setStatus(driver.getStatus().toString());
    }
    
    // Getters and Setters
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVehicleNumber() { return vehicleNumber; }
    public void setVehicleNumber(String vehicleNumber) { this.vehicleNumber = vehicleNumber; }
    
    public Driver.DriverStatus getStatus() { return status; }
    public void setStatus(Driver.DriverStatus status) { this.status = status; }
    
    public LocalDateTime getShiftStartTime() { return shiftStartTime; }
    public void setShiftStartTime(LocalDateTime shiftStartTime) { this.shiftStartTime = shiftStartTime; }
    
    public Integer getActiveDeliveries() { return activeDeliveries; }
    public void setActiveDeliveries(Integer activeDeliveries) { this.activeDeliveries = activeDeliveries; }
    
    public Integer getCompletedToday() { return completedToday; }
    public void setCompletedToday(Integer completedToday) { this.completedToday = completedToday; }
    
    public Double getTotalDistanceToday() { return totalDistanceToday; }
    public void setTotalDistanceToday(Double totalDistanceToday) { this.totalDistanceToday = totalDistanceToday; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public CurrentRouteInfo getCurrentRoute() { return currentRoute; }
    public void setCurrentRoute(CurrentRouteInfo currentRoute) { this.currentRoute = currentRoute; }
    
    public ShiftInfo getShiftInfo() { return shiftInfo; }
    public void setShiftInfo(ShiftInfo shiftInfo) { this.shiftInfo = shiftInfo; }
    
    // Inner classes for nested data
    public static class CurrentRouteInfo {
        private String routeId;
        private String routeName;
        private Integer totalStops;
        private Integer completedStops;
        private String estimatedTime;
        
        // Getters and Setters
        public String getRouteId() { return routeId; }
        public void setRouteId(String routeId) { this.routeId = routeId; }
        
        public String getRouteName() { return routeName; }
        public void setRouteName(String routeName) { this.routeName = routeName; }
        
        public Integer getTotalStops() { return totalStops; }
        public void setTotalStops(Integer totalStops) { this.totalStops = totalStops; }
        
        public Integer getCompletedStops() { return completedStops; }
        public void setCompletedStops(Integer completedStops) { this.completedStops = completedStops; }
        
        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    }
    
    public static class ShiftInfo {
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private Long durationMinutes;
        
        // Getters and Setters
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { 
            this.startTime = startTime;
            updateDuration();
        }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { 
            this.endTime = endTime;
            updateDuration();
        }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Long getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Long durationMinutes) { this.durationMinutes = durationMinutes; }
        
        private void updateDuration() {
            if (startTime != null) {
                LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
                this.durationMinutes = java.time.Duration.between(startTime, end).toMinutes();
            }
        }
    }
}
