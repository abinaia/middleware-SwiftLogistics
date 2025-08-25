package com.swiftlogistics.middleware.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {
    
    @NotNull
    private Long clientId;
    
    @NotBlank
    private String deliveryAddress;
    
    @NotBlank
    private String recipientName;
    
    private String recipientPhone;
    private String packageDescription;
    private Double packageWeight;
    private String packageDimensions;
    
    // Constructors
    public OrderRequest() {}
    
    public OrderRequest(Long clientId, String deliveryAddress, String recipientName) {
        this.clientId = clientId;
        this.deliveryAddress = deliveryAddress;
        this.recipientName = recipientName;
    }
    
    // Getters and Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    
    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }
    
    public String getPackageDescription() { return packageDescription; }
    public void setPackageDescription(String packageDescription) { this.packageDescription = packageDescription; }
    
    public Double getPackageWeight() { return packageWeight; }
    public void setPackageWeight(Double packageWeight) { this.packageWeight = packageWeight; }
    
    public String getPackageDimensions() { return packageDimensions; }
    public void setPackageDimensions(String packageDimensions) { this.packageDimensions = packageDimensions; }
}
