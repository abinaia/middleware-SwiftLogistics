
package com.swiftlogistics.middleware.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:19006", 
    "http://172.19.176.1:3000",
    "http://172.20.10.4:8080",
    "http://172.19.176.1:8080"
})
public class DashboardController {

    @GetMapping("/stats/{clientId}")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@PathVariable Long clientId) {
        // Create mock dashboard stats for demonstration
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", 4);
        stats.put("activeOrders", 3);
        stats.put("pendingOrders", 3);
        stats.put("deliveredOrders", 1);
        stats.put("cancelledOrders", 0);
        stats.put("totalRevenue", 25000.0);
        stats.put("averageDeliveryTime", 2.5);
        
        // Add recent orders array (empty for now, but could be populated from OrderService)
        stats.put("recentOrders", new java.util.ArrayList<>());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Dashboard API");
        return ResponseEntity.ok(response);
    }
}
