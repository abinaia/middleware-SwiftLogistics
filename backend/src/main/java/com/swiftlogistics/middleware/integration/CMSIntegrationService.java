package com.swiftlogistics.middleware.integration;

import com.swiftlogistics.middleware.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Service for integrating with the legacy CMS (Client Management System)
 * Uses SOAP/XML protocol for communication
 */
@Service
public class CMSIntegrationService {

    private final WebServiceTemplate webServiceTemplate;

    @Value("${external.cms.url}")
    private String cmsUrl;

    public CMSIntegrationService() {
        this.webServiceTemplate = new WebServiceTemplate();
    }

    /**
     * Submit order to CMS system
     * Converts order data to SOAP XML format
     */
    public void submitOrder(Order order) {
        try {
            // Create SOAP request XML
            String soapRequest = createSoapOrderRequest(order);
            
            // Log the request (for debugging)
            System.out.println("Sending SOAP request to CMS: " + soapRequest);
            
            // For prototype, we'll simulate the SOAP call
            simulateCMSCall(order);
            
            System.out.println("Order " + order.getOrderNumber() + " submitted to CMS successfully");
            
        } catch (Exception e) {
            System.err.println("Failed to submit order to CMS: " + e.getMessage());
            throw new RuntimeException("CMS integration failed", e);
        }
    }

    private String createSoapOrderRequest(Order order) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
               "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
               "<soap:Body>" +
               "<SubmitOrder>" +
               "<OrderNumber>" + order.getOrderNumber() + "</OrderNumber>" +
               "<ClientId>" + order.getClient().getId() + "</ClientId>" +
               "<DeliveryAddress>" + order.getDeliveryAddress() + "</DeliveryAddress>" +
               "<RecipientName>" + order.getRecipientName() + "</RecipientName>" +
               "<RecipientPhone>" + order.getRecipientPhone() + "</RecipientPhone>" +
               "</SubmitOrder>" +
               "</soap:Body>" +
               "</soap:Envelope>";
    }

    private void simulateCMSCall(Order order) {
        // Simulate network delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate successful response
        System.out.println("CMS Response: Order " + order.getOrderNumber() + " accepted");
    }

    /**
     * Get order status from CMS
     */
    public String getOrderStatus(String orderNumber) {
        try {
            // Simulate CMS status check
            System.out.println("Checking order status in CMS for: " + orderNumber);
            return "ACCEPTED";
        } catch (Exception e) {
            System.err.println("Failed to get order status from CMS: " + e.getMessage());
            throw new RuntimeException("CMS status check failed", e);
        }
    }
}
