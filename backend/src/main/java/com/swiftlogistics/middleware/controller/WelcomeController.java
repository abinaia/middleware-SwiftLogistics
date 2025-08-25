package com.swiftlogistics.middleware.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to SwiftLogistics Middleware API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("description", "Middleware for integrating CMS, ROS, and WMS systems");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("Health Check", "/actuator/health");
        endpoints.put("Create Order", "POST /orders");
        endpoints.put("Get Order", "GET /orders/{orderId}");
        endpoints.put("Track Order", "GET /orders/track/{trackingNumber}");
        endpoints.put("Client Orders", "GET /orders/client/{clientId}");
        endpoints.put("All Orders", "GET /orders");
        
        response.put("available_endpoints", endpoints);
        response.put("note", "Database operations are currently disabled for testing");
        
        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("database", "DISABLED");
        response.put("rabbitmq", "DISCONNECTED");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "SwiftLogistics Middleware");
        response.put("version", "1.0.0");
        response.put("description", "Integration middleware for delivery management systems");
        
        Map<String, String> integrations = new HashMap<>();
        integrations.put("CMS", "Client Management System - SOAP/XML");
        integrations.put("ROS", "Route Optimization System - REST/JSON");
        integrations.put("WMS", "Warehouse Management System - TCP/IP");
        
        response.put("target_integrations", integrations);
        response.put("current_status", "Development Mode - Core APIs Available");
        
        return response;
    }
}
