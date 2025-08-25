package com.swiftlogistics.middleware.integration;

import com.swiftlogistics.middleware.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * Service for integrating with ROS (Route Optimization System)
 * Uses RESTful API with JSON for communication
 */
@Service
public class ROSIntegrationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${external.ros.url}")
    private String rosUrl;

    public ROSIntegrationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Submit delivery address to ROS for route planning
     */
    public void planRoute(Order order) {
        try {
            // Create JSON request for route planning
            Map<String, Object> routeRequest = createRouteRequest(order);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(routeRequest, headers);
            
            // Log the request
            System.out.println("Sending REST request to ROS: " + objectMapper.writeValueAsString(routeRequest));
            
            // For prototype, simulate the REST call
            simulateROSCall(order);
            
            System.out.println("Route planned for order " + order.getOrderNumber() + " via ROS");
            
        } catch (Exception e) {
            System.err.println("Failed to plan route with ROS: " + e.getMessage());
            throw new RuntimeException("ROS integration failed", e);
        }
    }

    private Map<String, Object> createRouteRequest(Order order) {
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", order.getId());
        request.put("deliveryAddress", order.getDeliveryAddress());
        request.put("priority", "NORMAL");
        request.put("vehicleType", "VAN");
        
        // Add delivery time window
        Map<String, String> timeWindow = new HashMap<>();
        timeWindow.put("start", "09:00");
        timeWindow.put("end", "17:00");
        request.put("deliveryWindow", timeWindow);
        
        return request;
    }

    private void simulateROSCall(Order order) {
        // Simulate network delay
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate route optimization response
        System.out.println("ROS Response: Route optimized for delivery to " + order.getDeliveryAddress());
        System.out.println("Estimated delivery time: 2-4 hours");
        System.out.println("Assigned vehicle: VAN-001");
    }

    /**
     * Get optimized route for multiple deliveries
     */
    public Map<String, Object> getOptimizedRoute(List<Order> orders) {
        try {
            // Create batch route request
            Map<String, Object> batchRequest = new HashMap<>();
            batchRequest.put("deliveries", orders.stream().map(this::createRouteRequest).toList());
            
            // Simulate batch optimization
            Map<String, Object> response = new HashMap<>();
            response.put("routeId", "ROUTE-" + System.currentTimeMillis());
            response.put("totalDistance", "45.7 km");
            response.put("estimatedTime", "4.5 hours");
            response.put("deliverySequence", Arrays.asList(1, 3, 2, 4));
            
            System.out.println("Batch route optimized for " + orders.size() + " deliveries");
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Failed to get optimized route from ROS: " + e.getMessage());
            throw new RuntimeException("ROS batch optimization failed", e);
        }
    }

    /**
     * Update delivery status in ROS
     */
    public void updateDeliveryStatus(Order order, String status) {
        try {
            Map<String, Object> statusUpdate = new HashMap<>();
            statusUpdate.put("orderId", order.getId());
            statusUpdate.put("status", status);
            statusUpdate.put("timestamp", System.currentTimeMillis());
            
            System.out.println("Updating delivery status in ROS: " + order.getOrderNumber() + " -> " + status);
            
            // Simulate status update
            Thread.sleep(500);
            System.out.println("ROS: Status updated successfully");
            
        } catch (Exception e) {
            System.err.println("Failed to update status in ROS: " + e.getMessage());
            throw new RuntimeException("ROS status update failed", e);
        }
    }
}
