package com.swiftlogistics.middleware.dto;

import java.time.LocalDateTime;

/**
 * DTO for order status updates
 */
public class OrderStatusUpdate {
    private Long orderId;
    private String orderNumber;
    private String status;
    private LocalDateTime updatedAt;
    private Long clientId;
    private String location;
    private String message;

    // Constructors
    public OrderStatusUpdate() {}

    public OrderStatusUpdate(Long orderId, String orderNumber, String status, Long clientId) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.status = status;
        this.clientId = clientId;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "OrderStatusUpdate{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", status='" + status + '\'' +
                ", updatedAt=" + updatedAt +
                ", clientId=" + clientId +
                '}';
    }
}
