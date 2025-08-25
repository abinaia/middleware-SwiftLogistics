package com.swiftlogistics.middleware.integration;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.model.Package;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Service for integrating with WMS (Warehouse Management System)
 * Uses proprietary TCP/IP messaging protocol
 */
@Service
public class WMSIntegrationService {

    @Value("${external.wms.host}")
    private String wmsHost;

    @Value("${external.wms.port}")
    private int wmsPort;

    /**
     * Add package to warehouse system
     */
    public void addPackageToWarehouse(Order order) {
        try {
            // Create WMS message
            String wmsMessage = createWMSMessage(order);
            
            // Send via TCP/IP
            sendTCPMessage(wmsMessage);
            
            System.out.println("Package added to WMS for order: " + order.getOrderNumber());
            
        } catch (Exception e) {
            System.err.println("Failed to add package to WMS: " + e.getMessage());
            throw new RuntimeException("WMS integration failed", e);
        }
    }

    private String createWMSMessage(Order order) {
        // Create proprietary message format
        StringBuilder message = new StringBuilder();
        message.append("WMS_COMMAND:ADD_PACKAGE\n");
        message.append("ORDER_ID:").append(order.getId()).append("\n");
        message.append("ORDER_NUMBER:").append(order.getOrderNumber()).append("\n");
        message.append("CLIENT_ID:").append(order.getClient().getId()).append("\n");
        message.append("DELIVERY_ADDRESS:").append(order.getDeliveryAddress()).append("\n");
        message.append("RECIPIENT:").append(order.getRecipientName()).append("\n");
        message.append("TIMESTAMP:").append(System.currentTimeMillis()).append("\n");
        message.append("END_MESSAGE\n");
        
        return message.toString();
    }

    private void sendTCPMessage(String message) {
        // For prototype, simulate TCP connection
        try {
            simulateTCPConnection(message);
        } catch (Exception e) {
            System.err.println("TCP connection simulation failed: " + e.getMessage());
            // In real implementation, this would be actual TCP socket connection
        }
    }

    private void simulateTCPConnection(String message) throws InterruptedException {
        // Simulate TCP connection establishment
        System.out.println("Establishing TCP connection to WMS at " + wmsHost + ":" + wmsPort);
        Thread.sleep(500);
        
        // Simulate sending message
        System.out.println("Sending WMS message:");
        System.out.println(message);
        Thread.sleep(300);
        
        // Simulate response
        String response = "WMS_RESPONSE:SUCCESS\nPACKAGE_ID:PKG-" + System.currentTimeMillis() + "\nLOCATION:A1-B2-C3\nSTATUS:RECEIVED\n";
        System.out.println("WMS Response:");
        System.out.println(response);
    }

    /**
     * Update package status in WMS
     */
    public void updatePackageStatus(Package pkg, Package.PackageStatus status) {
        try {
            String message = createStatusUpdateMessage(pkg, status);
            sendTCPMessage(message);
            
            System.out.println("Package status updated in WMS: " + pkg.getPackageId() + " -> " + status);
            
        } catch (Exception e) {
            System.err.println("Failed to update package status in WMS: " + e.getMessage());
            throw new RuntimeException("WMS status update failed", e);
        }
    }

    private String createStatusUpdateMessage(Package pkg, Package.PackageStatus status) {
        StringBuilder message = new StringBuilder();
        message.append("WMS_COMMAND:UPDATE_STATUS\n");
        message.append("PACKAGE_ID:").append(pkg.getPackageId()).append("\n");
        message.append("NEW_STATUS:").append(status.name()).append("\n");
        message.append("TIMESTAMP:").append(System.currentTimeMillis()).append("\n");
        message.append("END_MESSAGE\n");
        
        return message.toString();
    }

    /**
     * Get package location from WMS
     */
    public String getPackageLocation(String packageId) {
        try {
            String message = "WMS_COMMAND:GET_LOCATION\nPACKAGE_ID:" + packageId + "\nEND_MESSAGE\n";
            sendTCPMessage(message);
            
            // Simulate location response
            String location = "A" + (int)(Math.random() * 10) + "-B" + (int)(Math.random() * 20) + "-C" + (int)(Math.random() * 15);
            System.out.println("Package " + packageId + " location: " + location);
            
            return location;
            
        } catch (Exception e) {
            System.err.println("Failed to get package location from WMS: " + e.getMessage());
            throw new RuntimeException("WMS location query failed", e);
        }
    }

    /**
     * Mark package as picked for delivery
     */
    public void markPackagePicked(String packageId, String driverId) {
        try {
            StringBuilder message = new StringBuilder();
            message.append("WMS_COMMAND:MARK_PICKED\n");
            message.append("PACKAGE_ID:").append(packageId).append("\n");
            message.append("DRIVER_ID:").append(driverId).append("\n");
            message.append("TIMESTAMP:").append(System.currentTimeMillis()).append("\n");
            message.append("END_MESSAGE\n");
            
            sendTCPMessage(message.toString());
            
            System.out.println("Package " + packageId + " marked as picked by driver " + driverId);
            
        } catch (Exception e) {
            System.err.println("Failed to mark package as picked in WMS: " + e.getMessage());
            throw new RuntimeException("WMS pick operation failed", e);
        }
    }
}
