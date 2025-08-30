package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.service.DeliveryManagementService;
import com.swiftlogistics.middleware.service.GPSTrackingService;
import com.swiftlogistics.middleware.service.RouteOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

/**
 * REST Controller for real route and delivery management
 * Replaces mock routes with actual delivery tracking and optimization
 */
@RestController
@RequestMapping("/routes")
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:19006", 
    "http://172.19.176.1:3000",
    "http://172.20.10.4:8080",
    "http://172.19.176.1:8080"
})
public class RouteManagementController {

    @Autowired
    private DeliveryManagementService deliveryManagementService;

    @Autowired
    private GPSTrackingService gpsTrackingService;

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    /**
     * Automatically assign deliveries to available drivers
     */
    @PostMapping("/assign")
    public ResponseEntity<Map<String, Object>> assignDeliveries() {
        try {
            Map<String, Object> result = deliveryManagementService.assignDeliveriesToDrivers();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to assign deliveries: " + e.getMessage()));
        }
    }

    /**
     * Get real-time status of all active deliveries
     */
    @GetMapping("/active")
    public ResponseEntity<List<Map<String, Object>>> getActiveDeliveries() {
        try {
            List<Map<String, Object>> activeDeliveries = 
                deliveryManagementService.getActiveDeliveryStatus();
            return ResponseEntity.ok(activeDeliveries);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(List.of(Map.of("error", "Failed to get delivery status: " + e.getMessage())));
        }
    }

    /**
     * Update driver's real-time location
     */
    @PostMapping("/location/{driverId}")
    public ResponseEntity<Map<String, String>> updateDriverLocation(
            @PathVariable String driverId,
            @RequestBody Map<String, Object> locationData) {
        try {
            double latitude = ((Number) locationData.get("latitude")).doubleValue();
            double longitude = ((Number) locationData.get("longitude")).doubleValue();
            double speed = locationData.containsKey("speed") ? 
                ((Number) locationData.get("speed")).doubleValue() : 0.0;
            double heading = locationData.containsKey("heading") ? 
                ((Number) locationData.get("heading")).doubleValue() : 0.0;

            gpsTrackingService.updateDriverLocation(driverId, latitude, longitude, 
                speed, heading, new java.util.Date());

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Location updated for driver " + driverId
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid location data: " + e.getMessage()));
        }
    }

    /**
     * Get live tracking data for a specific driver
     */
    @GetMapping("/tracking/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverTracking(@PathVariable String driverId) {
        try {
            Map<String, Object> trackingData = gpsTrackingService.getLiveTrackingData(driverId);
            return ResponseEntity.ok(trackingData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get tracking data: " + e.getMessage()));
        }
    }

    /**
     * Calculate ETA to a specific address
     */
    @GetMapping("/eta/{driverId}")
    public ResponseEntity<Map<String, Object>> calculateETA(
            @PathVariable String driverId,
            @RequestParam String destination) {
        try {
            Map<String, Object> eta = gpsTrackingService.calculateETA(driverId, destination);
            return ResponseEntity.ok(eta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to calculate ETA: " + e.getMessage()));
        }
    }

    /**
     * Complete a delivery
     */
    @PostMapping("/delivery/complete")
    public ResponseEntity<Map<String, Object>> completeDelivery(
            @RequestBody Map<String, Object> deliveryData) {
        try {
            String driverId = (String) deliveryData.get("driverId");
            String orderNumber = (String) deliveryData.get("orderNumber");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) deliveryData.get("details");
            
            Map<String, Object> result = deliveryManagementService.completeDelivery(
                driverId, orderNumber, details != null ? details : Map.of());
                
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to complete delivery: " + e.getMessage()));
        }
    }

    /**
     * Get delivery analytics and performance metrics
     */
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getDeliveryAnalytics(
            @RequestParam(defaultValue = "today") String period) {
        try {
            Map<String, Object> analytics = deliveryManagementService.getDeliveryAnalytics(period);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get analytics: " + e.getMessage()));
        }
    }

    /**
     * Get all driver locations for fleet management
     */
    @GetMapping("/fleet/locations")
    public ResponseEntity<Map<String, Object>> getFleetLocations() {
        try {
            Map<String, GPSTrackingService.DriverLocation> locations = 
                gpsTrackingService.getAllDriverLocations();
            
            // Convert to a more JSON-friendly format
            Map<String, Object> response = new java.util.HashMap<>();
            locations.forEach((driverId, location) -> {
                response.put(driverId, Map.of(
                    "latitude", location.getLatitude(),
                    "longitude", location.getLongitude(),
                    "speed", location.getSpeed(),
                    "heading", location.getHeading(),
                    "timestamp", location.getTimestamp()
                ));
            });

            return ResponseEntity.ok(Map.of(
                "status", "success",
                "driverCount", locations.size(),
                "locations", response
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get fleet locations: " + e.getMessage()));
        }
    }

    /**
     * Health check for route management system
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("service", "Route Management");
        health.put("features", List.of(
            "Real-time GPS tracking",
            "Route optimization",
            "Delivery assignment",
            "Performance analytics",
            "Fleet management"
        ));
        health.put("timestamp", new java.util.Date());
        
        return ResponseEntity.ok(health);
    }
}
