package com.swiftlogistics.middleware.dto;

/**
 * Represents a point in a delivery route with coordinates and metadata
 */
public class RoutePoint {
    private String address;
    private double latitude;
    private double longitude;
    private String pointId;
    private String orderReference;
    private String pointType; // START, DELIVERY, WAREHOUSE
    private Integer estimatedArrivalMinutes;
    private String status; // PENDING, COMPLETED, SKIPPED

    // Constructors
    public RoutePoint() {}

    public RoutePoint(String address, double latitude, double longitude, String pointId, String orderReference) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pointId = pointId;
        this.orderReference = orderReference;
        this.pointType = "DELIVERY";
        this.status = "PENDING";
    }

    public RoutePoint(String address, double latitude, double longitude, String pointId, String orderReference, String pointType) {
        this(address, latitude, longitude, pointId, orderReference);
        this.pointType = pointType;
    }

    // Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getPointType() {
        return pointType;
    }

    public void setPointType(String pointType) {
        this.pointType = pointType;
    }

    public Integer getEstimatedArrivalMinutes() {
        return estimatedArrivalMinutes;
    }

    public void setEstimatedArrivalMinutes(Integer estimatedArrivalMinutes) {
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RoutePoint{" +
                "address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", pointId='" + pointId + '\'' +
                ", orderReference='" + orderReference + '\'' +
                ", pointType='" + pointType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
