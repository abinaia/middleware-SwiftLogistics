package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.model.Driver;
import com.swiftlogistics.middleware.service.DriverService;
import com.swiftlogistics.middleware.service.GPSTrackingService;
import com.swiftlogistics.middleware.service.DeliveryManagementService;
import com.swiftlogistics.middleware.dto.DriverDashboardResponse;
import com.swiftlogistics.middleware.dto.DriverStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for driver operations
 */
@RestController
@RequestMapping("/drivers")
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://172.19.176.1:3000", 
    "http://172.20.10.4:8080",
    "http://172.19.176.1:8080"
}, allowCredentials = "false")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private GPSTrackingService gpsTrackingService;

    @Autowired
    private DeliveryManagementService deliveryManagementService;

    /**
     * Create a new driver
     */
    @PostMapping
    public ResponseEntity<Driver> createDriver(@RequestBody Driver driver) {
        try {
            Driver savedDriver = driverService.createDriver(
                driver.getDriverId(),
                driver.getName(), 
                driver.getEmail(),
                driver.getVehicleNumber()
            );
            return ResponseEntity.ok(savedDriver);
        } catch (Exception e) {
            System.err.println("Error creating driver: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get driver dashboard data with real-time information
     */
    @GetMapping("/{driverId}/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardData(@PathVariable String driverId) {
        try {
            // Get basic driver information
            DriverDashboardResponse basicData = driverService.getDashboardData(driverId);
            
            // Create enhanced dashboard with real-time data
            Map<String, Object> enhancedDashboard = new HashMap<>();
            
            // Basic driver info
            enhancedDashboard.put("driverId", basicData.getDriverId());
            enhancedDashboard.put("name", basicData.getName());
            enhancedDashboard.put("vehicleNumber", basicData.getVehicleNumber());
            enhancedDashboard.put("status", basicData.getStatus());
            enhancedDashboard.put("shiftStartTime", basicData.getShiftStartTime());
            
            // Real-time location and tracking data
            GPSTrackingService.DriverLocation currentLocation = 
                gpsTrackingService.getCurrentLocation(driverId);
            
            if (currentLocation != null) {
                enhancedDashboard.put("currentLocation", Map.of(
                    "latitude", currentLocation.getLatitude(),
                    "longitude", currentLocation.getLongitude(),
                    "speed", currentLocation.getSpeed(),
                    "heading", currentLocation.getHeading(),
                    "lastUpdated", currentLocation.getTimestamp()
                ));
            }
            
            // Live tracking data (route progress, deliveries, etc.)
            Map<String, Object> liveTracking = gpsTrackingService.getLiveTrackingData(driverId);
            enhancedDashboard.put("liveTracking", liveTracking);
            
            // Performance statistics
            enhancedDashboard.put("activeDeliveries", basicData.getActiveDeliveries());
            enhancedDashboard.put("completedToday", basicData.getCompletedToday());
            enhancedDashboard.put("totalDistanceToday", basicData.getTotalDistanceToday());
            enhancedDashboard.put("averageRating", basicData.getAverageRating());
            
            // Add real-time route information if driver is on route
            GPSTrackingService.RouteProgress routeProgress = 
                gpsTrackingService.getRouteProgress(driverId);
            
            if (routeProgress != null) {
                enhancedDashboard.put("currentRoute", Map.of(
                    "routeId", routeProgress.getRoute().getRouteId(),
                    "totalDeliveries", routeProgress.getRoute().getTotalDeliveryPoints(),
                    "completedDeliveries", routeProgress.getCompletedDeliveries().size(),
                    "progressPercentage", routeProgress.getRoute().getProgressPercentage(),
                    "estimatedCompletion", "Calculating..." // Could add ETA calculation
                ));
            }
            
            enhancedDashboard.put("dataType", "REAL_TIME");
            enhancedDashboard.put("timestamp", new java.util.Date());
            
            return ResponseEntity.ok(enhancedDashboard);
            
        } catch (Exception e) {
            System.err.println("Error fetching dashboard data for driver " + driverId + ": " + e.getMessage());
            
            // Fallback to basic data if real-time services fail
            try {
                DriverDashboardResponse basicData = driverService.getDashboardData(driverId);
                Map<String, Object> fallbackDashboard = new HashMap<>();
                fallbackDashboard.put("driverId", basicData.getDriverId());
                fallbackDashboard.put("name", basicData.getName());
                fallbackDashboard.put("vehicleNumber", basicData.getVehicleNumber());
                fallbackDashboard.put("status", basicData.getStatus());
                fallbackDashboard.put("shiftStartTime", basicData.getShiftStartTime());
                fallbackDashboard.put("activeDeliveries", basicData.getActiveDeliveries());
                fallbackDashboard.put("completedToday", basicData.getCompletedToday());
                fallbackDashboard.put("totalDistanceToday", basicData.getTotalDistanceToday());
                fallbackDashboard.put("averageRating", basicData.getAverageRating());
                fallbackDashboard.put("dataType", "BASIC");
                fallbackDashboard.put("note", "Real-time features unavailable");
                
                return ResponseEntity.ok(fallbackDashboard);
            } catch (Exception fallbackError) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Driver not found: " + driverId));
            }
        }
    }

    /**
     * Get driver profile
     */
    @GetMapping("/{driverId}")
    public ResponseEntity<Driver> getDriverProfile(@PathVariable String driverId) {
        try {
            Driver driver = driverService.getDriverByDriverId(driverId);
            return ResponseEntity.ok(driver);
        } catch (Exception e) {
            System.err.println("Error fetching driver profile: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update driver status
     */
    @PutMapping("/{driverId}/status")
    public ResponseEntity<Map<String, Object>> updateDriverStatus(
            @PathVariable String driverId, 
            @RequestBody DriverStatusRequest request) {
        try {
            Driver updatedDriver = driverService.updateDriverStatus(driverId, request.getStatus());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Driver status updated successfully");
            response.put("driverId", driverId);
            response.put("newStatus", updatedDriver.getStatus());
            response.put("timestamp", updatedDriver.getUpdatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Start driver shift
     */
    @PostMapping("/{driverId}/shift/start")
    public ResponseEntity<Map<String, Object>> startShift(@PathVariable String driverId) {
        try {
            Driver driver = driverService.startShift(driverId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shift started successfully");
            response.put("driverId", driverId);
            response.put("shiftStartTime", driver.getShiftStartTime());
            response.put("status", driver.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * End driver shift
     */
    @PostMapping("/{driverId}/shift/end")
    public ResponseEntity<Map<String, Object>> endShift(@PathVariable String driverId) {
        try {
            Driver driver = driverService.endShift(driverId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shift ended successfully");
            response.put("driverId", driverId);
            response.put("shiftEndTime", driver.getShiftEndTime());
            response.put("status", driver.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get driver assigned deliveries
     */
    @GetMapping("/{driverId}/deliveries")
    public ResponseEntity<Map<String, Object>> getAssignedDeliveries(@PathVariable String driverId) {
        try {
            Map<String, Object> deliveries = new HashMap<>();
            deliveries.put("activeDeliveries", 0);
            deliveries.put("message", "No active deliveries at the moment");
            return ResponseEntity.ok(deliveries);
        } catch (Exception e) {
            System.err.println("Error fetching deliveries for driver " + driverId + ": " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get driver statistics
     */
    @GetMapping("/{driverId}/stats")
    public ResponseEntity<DriverDashboardResponse> getDriverStats(
            @PathVariable String driverId,
            @RequestParam(required = false) String date) {
        try {
            LocalDate targetDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            DriverDashboardResponse stats = driverService.getDriverStats(driverId, targetDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error fetching driver stats: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all drivers (for admin/testing)
     */
    @GetMapping
    public ResponseEntity<List<Driver>> getAllDrivers() {
        try {
            List<Driver> drivers = driverService.getAllDrivers();
            return ResponseEntity.ok(drivers);
        } catch (Exception e) {
            System.err.println("Error fetching all drivers: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Initialize default drivers (for testing)
     */
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeDrivers() {
        try {
            driverService.initializeDefaultDrivers();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Default drivers initialized successfully");
            response.put("drivers", driverService.getAllDrivers().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update delivery statistics (called when delivery is completed)
     */
    @PostMapping("/{driverId}/deliveries/{deliveryId}/complete")
    public ResponseEntity<Map<String, Object>> completeDelivery(
            @PathVariable String driverId,
            @PathVariable Long deliveryId,
            @RequestParam(required = false) Double distance) {
        try {
            // Update driver statistics
            driverService.updateDeliveryStats(driverId, distance);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery completed successfully");
            response.put("deliveryId", deliveryId);
            response.put("driverId", driverId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
