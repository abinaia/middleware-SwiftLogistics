package com.swiftlogistics.middleware.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "route_id", unique = true, nullable = false)
    private String routeId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "driver_id")
    private String driverId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;
    
    @Column(name = "total_stops")
    private Integer totalStops;
    
    @Column(name = "completed_stops")
    private Integer completedStops = 0;
    
    @Column(name = "estimated_time_minutes")
    private Integer estimatedTimeMinutes;
    
    @Column(name = "total_distance_km")
    private Double totalDistanceKm;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Delivery> deliveries;
    
    // Enum for route status
    public enum RouteStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
    
    // Constructors
    public Route() {}
    
    public Route(String routeId, String name, String driverId) {
        this.routeId = routeId;
        this.name = name;
        this.driverId = driverId;
        this.status = RouteStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    
    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }
    
    public Integer getTotalStops() { return totalStops; }
    public void setTotalStops(Integer totalStops) { this.totalStops = totalStops; }
    
    public Integer getCompletedStops() { return completedStops; }
    public void setCompletedStops(Integer completedStops) { this.completedStops = completedStops; }
    
    public Integer getEstimatedTimeMinutes() { return estimatedTimeMinutes; }
    public void setEstimatedTimeMinutes(Integer estimatedTimeMinutes) { this.estimatedTimeMinutes = estimatedTimeMinutes; }
    
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public List<Delivery> getDeliveries() { return deliveries; }
    public void setDeliveries(List<Delivery> deliveries) { this.deliveries = deliveries; }
    
    // Utility methods
    public double getProgressPercentage() {
        if (totalStops == null || totalStops == 0) return 0.0;
        return (double) completedStops / totalStops * 100.0;
    }
    
    public String getFormattedEstimatedTime() {
        if (estimatedTimeMinutes == null) return "Unknown";
        int hours = estimatedTimeMinutes / 60;
        int minutes = estimatedTimeMinutes % 60;
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    @PreUpdate
    public void updateCompletedStops() {
        if (deliveries != null) {
            this.completedStops = (int) deliveries.stream()
                .filter(d -> d.getStatus() == Delivery.DeliveryStatus.COMPLETED)
                .count();
            this.totalStops = deliveries.size();
        }
    }
}
