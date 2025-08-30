package com.swiftlogistics.middleware.dto;

import java.util.Date;
import java.util.List;

/**
 * Represents an optimized delivery route with all necessary details
 */
public class OptimizedRoute {
    private String routeId;
    private List<RoutePoint> routePoints;
    private double totalDistanceKm;
    private int estimatedTimeMinutes;
    private Date createdAt;
    private Date lastUpdated;
    private String status; // PLANNED, ACTIVE, COMPLETED, CANCELLED
    private String routeType; // OPTIMIZED, FALLBACK, RECALCULATED
    private String driverId;
    private String vehicleId;
    private Double fuelEstimateL;
    private String trafficCondition; // LOW, MEDIUM, HIGH

    // Constructors
    public OptimizedRoute() {}

    public OptimizedRoute(String routeId, List<RoutePoint> routePoints, double totalDistanceKm, 
                         int estimatedTimeMinutes, Date createdAt, String routeType) {
        this.routeId = routeId;
        this.routePoints = routePoints;
        this.totalDistanceKm = totalDistanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.createdAt = createdAt;
        this.lastUpdated = createdAt;
        this.routeType = routeType;
        this.status = "PLANNED";
    }

    // Getters and Setters
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public List<RoutePoint> getRoutePoints() {
        return routePoints;
    }

    public void setRoutePoints(List<RoutePoint> routePoints) {
        this.routePoints = routePoints;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public int getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(int estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRouteType() {
        return routeType;
    }

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getFuelEstimateL() {
        return fuelEstimateL;
    }

    public void setFuelEstimateL(Double fuelEstimateL) {
        this.fuelEstimateL = fuelEstimateL;
    }

    public String getTrafficCondition() {
        return trafficCondition;
    }

    public void setTrafficCondition(String trafficCondition) {
        this.trafficCondition = trafficCondition;
    }

    // Utility methods
    public int getTotalDeliveryPoints() {
        if (routePoints == null) return 0;
        return (int) routePoints.stream()
            .filter(point -> "DELIVERY".equals(point.getPointType()))
            .count();
    }

    public int getCompletedDeliveries() {
        if (routePoints == null) return 0;
        return (int) routePoints.stream()
            .filter(point -> "DELIVERY".equals(point.getPointType()) && 
                           "COMPLETED".equals(point.getStatus()))
            .count();
    }

    public double getProgressPercentage() {
        int total = getTotalDeliveryPoints();
        if (total == 0) return 0.0;
        return (double) getCompletedDeliveries() / total * 100.0;
    }

    @Override
    public String toString() {
        return "OptimizedRoute{" +
                "routeId='" + routeId + '\'' +
                ", totalDistanceKm=" + totalDistanceKm +
                ", estimatedTimeMinutes=" + estimatedTimeMinutes +
                ", status='" + status + '\'' +
                ", routeType='" + routeType + '\'' +
                ", deliveryPoints=" + getTotalDeliveryPoints() +
                ", completed=" + getCompletedDeliveries() +
                '}';
    }
}
