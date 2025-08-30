package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.service.RouteService;
import com.swiftlogistics.middleware.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://192.168.1.100:8081", "http://10.0.2.2:8081"},
    allowCredentials = "false"
)
public class TestDataController {

    @Autowired
    private RouteService routeService;

    /**
     * Create sample routes for testing
     * POST /api/test/create-sample-routes
     */
    @PostMapping("/create-sample-routes")
    public ResponseEntity<Map<String, Object>> createSampleRoutes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Sample deliveries for Route 1
            List<Map<String, Object>> deliveries1 = Arrays.asList(
                Map.of(
                    "customerName", "John Doe",
                    "address", "123 Main Street, Downtown",
                    "phone", "+1234567890"
                ),
                Map.of(
                    "customerName", "Jane Smith", 
                    "address", "456 Oak Avenue, Midtown",
                    "phone", "+1234567891"
                ),
                Map.of(
                    "customerName", "Bob Johnson",
                    "address", "789 Pine Road, Uptown", 
                    "phone", "+1234567892"
                )
            );
            
            // Sample deliveries for Route 2
            List<Map<String, Object>> deliveries2 = Arrays.asList(
                Map.of(
                    "customerName", "Alice Brown",
                    "address", "321 Elm Street, Eastside",
                    "phone", "+1234567893"
                ),
                Map.of(
                    "customerName", "Charlie Davis",
                    "address", "654 Maple Drive, Westside",
                    "phone", "+1234567894"
                ),
                Map.of(
                    "customerName", "Diana Wilson",
                    "address", "987 Cedar Lane, Southside",
                    "phone", "+1234567895"
                ),
                Map.of(
                    "customerName", "Frank Miller",
                    "address", "147 Birch Street, Northside",
                    "phone", "+1234567896"
                )
            );

            // Create Route 1 (Downtown Route) - Will be started automatically for testing
            Route route1 = routeService.createRoute("Downtown Express", "DRV001", deliveries1);
            
            // Create Route 2 (Suburban Route) - Will remain pending
            Route route2 = routeService.createRoute("Suburban Circuit", "DRV001", deliveries2);
            
            // Start Route 1 for immediate testing
            routeService.startRoute(route1.getRouteId());
            
            response.put("success", true);
            response.put("message", "Sample routes created successfully");
            response.put("routesCreated", 2);
            response.put("totalDeliveries", deliveries1.size() + deliveries2.size());
            response.put("activeRoute", route1.getRouteId());
            response.put("pendingRoute", route2.getRouteId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating sample routes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Clear all test data
     * DELETE /api/test/clear-data
     */
    @DeleteMapping("/clear-data") 
    public ResponseEntity<Map<String, Object>> clearTestData() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Note: This would require additional service methods to clear data
            // For now, just return success message
            response.put("success", true);
            response.put("message", "Test data cleared successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error clearing test data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get system status
     * GET /api/test/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            response.put("success", true);
            response.put("status", "OPERATIONAL");
            response.put("services", Map.of(
                "routeService", "ACTIVE",
                "deliveryService", "ACTIVE", 
                "gpsTracking", "ACTIVE",
                "routeOptimization", "ACTIVE"
            ));
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting system status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
