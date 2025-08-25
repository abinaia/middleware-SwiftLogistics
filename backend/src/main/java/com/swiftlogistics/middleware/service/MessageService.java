package com.swiftlogistics.middleware.service;

import org.springframework.stereotype.Service;

/**
 * MessageService - Temporarily disabled due to RabbitMQ connection issues
 * This service will be re-enabled once RabbitMQ is properly configured
 */
@Service
public class MessageService {
    
    /**
     * Placeholder method to prevent compilation errors
     * All messaging functionality is temporarily disabled
     */
    public void placeholder() {
        System.out.println("MessageService is temporarily disabled - RabbitMQ not configured");
    }
    
    /**
     * Temporary method for sending order processing messages
     * @param order The order to process (currently does nothing)
     */
    public void sendOrderForProcessing(Object order) {
        System.out.println("Order processing temporarily disabled - would process: " + order);
    }
    
    /**
     * Temporary method for sending status updates
     * @param order The order with status update (currently does nothing)
     */
    public void sendStatusUpdate(Object order) {
        System.out.println("Status update temporarily disabled - would update: " + order);
    }
}