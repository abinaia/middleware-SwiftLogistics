package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.model.Route;
import com.swiftlogistics.middleware.model.Delivery;
import com.swiftlogistics.middleware.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(
    origins = {"http://localhost:3000", "http://192.168.1.100:8081", "http://10.0.2.2:8081"},
    allowCredentials = "false"
)
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * Create a new route
     * POST /api/routes
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRoute(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String routeName = (String) request.get("routeName");
            String driverId = (String) request.get("driverId");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> deliveries = (List<Map<String, Object>>) request.get("deliveries");
            
            if (routeName == null || driverId == null || deliveries == null || deliveries.isEmpty()) {
                response.put("success", false);
                response.put("message", "Missing required fields: routeName, driverId, deliveries");
                return ResponseEntity.badRequest().body(response);
            }
            
            Route route = routeService.createRoute(routeName, driverId, deliveries);
            
            response.put("success", true);
            response.put("message", "Route created successfully");
            response.put("route", route);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating route: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get routes for a driver
     * GET /api/routes/driver/{driverId}
     */
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getDriverRoutes(@PathVariable String driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Route> routes = routeService.getDriverRoutes(driverId);
            
            response.put("success", true);
            response.put("routes", routes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching routes: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get active route for a driver
     * GET /api/routes/driver/{driverId}/active
     */
    @GetMapping("/driver/{driverId}/active")
    public ResponseEntity<Map<String, Object>> getActiveRoute(@PathVariable String driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Route activeRoute = routeService.getActiveRoute(driverId);
            
            response.put("success", true);
            response.put("route", activeRoute);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching active route: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Start a route
     * POST /api/routes/{routeId}/start
     */
    @PostMapping("/{routeId}/start")
    public ResponseEntity<Map<String, Object>> startRoute(@PathVariable String routeId) {
        Map<String, Object> result = routeService.startRoute(routeId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Get deliveries for a route
     * GET /api/routes/{routeId}/deliveries
     */
    @GetMapping("/{routeId}/deliveries")
    public ResponseEntity<Map<String, Object>> getRouteDeliveries(@PathVariable String routeId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Delivery> deliveries = routeService.getRouteDeliveries(routeId);
            
            response.put("success", true);
            response.put("deliveries", deliveries);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching deliveries: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get pending deliveries for a driver
     * GET /api/routes/driver/{driverId}/deliveries/pending
     */
    @GetMapping("/driver/{driverId}/deliveries/pending")
    public ResponseEntity<Map<String, Object>> getPendingDeliveries(@PathVariable String driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Delivery> deliveries = routeService.getPendingDeliveries(driverId);
            
            response.put("success", true);
            response.put("deliveries", deliveries);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching pending deliveries: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Complete a delivery
     * POST /api/routes/deliveries/{deliveryId}/complete
     */
    @PostMapping("/deliveries/{deliveryId}/complete")
    public ResponseEntity<Map<String, Object>> completeDelivery(
            @PathVariable String deliveryId,
            @RequestBody Map<String, Object> request) {
        
        String notes = (String) request.get("notes");
        String signatureUrl = (String) request.get("signatureUrl");
        
        Map<String, Object> result = routeService.completeDelivery(deliveryId, notes, signatureUrl);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Get route statistics
     * GET /api/routes/stats/driver/{driverId}
     */
    @GetMapping("/stats/driver/{driverId}")
    public ResponseEntity<Map<String, Object>> getRouteStats(@PathVariable String driverId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Route> allRoutes = routeService.getDriverRoutes(driverId);
            Route activeRoute = routeService.getActiveRoute(driverId);
            List<Delivery> pendingDeliveries = routeService.getPendingDeliveries(driverId);
            
            long completedRoutes = allRoutes.stream()
                .filter(r -> r.getStatus() == Route.RouteStatus.COMPLETED)
                .count();
            
            double totalDistanceKm = allRoutes.stream()
                .mapToDouble(Route::getTotalDistanceKm)
                .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalRoutes", allRoutes.size());
            stats.put("completedRoutes", completedRoutes);
            stats.put("activeRoutes", activeRoute != null ? 1 : 0);
            stats.put("pendingDeliveries", pendingDeliveries.size());
            stats.put("totalDistanceCovered", Math.round(totalDistanceKm * 100.0) / 100.0);
            
            response.put("success", true);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error fetching route stats: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
