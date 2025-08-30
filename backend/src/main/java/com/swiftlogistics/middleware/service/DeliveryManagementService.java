package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.model.Driver;
import com.swiftlogistics.middleware.repository.OrderRepository;
import com.swiftlogistics.middleware.repository.DriverRepository;
import com.swiftlogistics.middleware.dto.OptimizedRoute;
import com.swiftlogistics.middleware.dto.RoutePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Real delivery management service
 * Handles actual delivery assignment, tracking, and completion
 */
@Service
public class DeliveryManagementService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    @Autowired
    private GPSTrackingService gpsTrackingService;

    /**
     * Assign deliveries to available drivers based on location and capacity
     */
    public Map<String, Object> assignDeliveriesToDrivers() {
        try {
            // Get all pending orders
            List<Order> pendingOrders = orderRepository.findByStatus(Order.OrderStatus.IN_WAREHOUSE);
            
            // Get all active drivers
            List<Driver> availableDrivers = driverRepository.findByStatus(Driver.DriverStatus.ACTIVE);

            Map<String, Object> assignmentResult = new HashMap<>();
            List<Map<String, Object>> assignments = new ArrayList<>();

            if (pendingOrders.isEmpty()) {
                assignmentResult.put("status", "NO_PENDING_ORDERS");
                assignmentResult.put("message", "No orders available for assignment");
                return assignmentResult;
            }

            if (availableDrivers.isEmpty()) {
                assignmentResult.put("status", "NO_AVAILABLE_DRIVERS");
                assignmentResult.put("message", "No active drivers available");
                return assignmentResult;
            }

            // Group orders by geographical area for efficient routing
            Map<String, List<Order>> ordersByArea = groupOrdersByArea(pendingOrders);

            // Assign orders to drivers
            for (Driver driver : availableDrivers) {
                if (pendingOrders.isEmpty()) break;

                // Determine how many orders this driver can handle
                int maxOrdersPerDriver = calculateDriverCapacity(driver);
                
                // Get best orders for this driver based on location
                List<Order> assignedOrders = selectOptimalOrdersForDriver(
                    driver, pendingOrders, maxOrdersPerDriver);

                if (!assignedOrders.isEmpty()) {
                    // Create optimized route
                    OptimizedRoute route = routeOptimizationService.calculateOptimizedRoute(
                        driver, assignedOrders);

                    // Update order statuses
                    assignedOrders.forEach(order -> {
                        order.setStatus(Order.OrderStatus.ROUTE_PLANNED);
                        orderRepository.save(order);
                    });

                    // Start route tracking
                    gpsTrackingService.startRouteTracking(driver.getDriverId(), route);

                    // Record assignment
                    Map<String, Object> assignment = new HashMap<>();
                    assignment.put("driverId", driver.getDriverId());
                    assignment.put("driverName", driver.getName());
                    assignment.put("vehicleNumber", driver.getVehicleNumber());
                    assignment.put("orderCount", assignedOrders.size());
                    assignment.put("orderNumbers", assignedOrders.stream()
                        .map(Order::getOrderNumber)
                        .collect(Collectors.toList()));
                    assignment.put("routeId", route.getRouteId());
                    assignment.put("estimatedDistance", route.getTotalDistanceKm());
                    assignment.put("estimatedTime", route.getEstimatedTimeMinutes());

                    assignments.add(assignment);

                    // Remove assigned orders from pending list
                    pendingOrders.removeAll(assignedOrders);

                    System.out.println("Assigned " + assignedOrders.size() + 
                                      " orders to driver " + driver.getDriverId());
                }
            }

            assignmentResult.put("status", "SUCCESS");
            assignmentResult.put("assignedDrivers", assignments.size());
            assignmentResult.put("totalOrdersAssigned", 
                assignments.stream().mapToInt(a -> (Integer) a.get("orderCount")).sum());
            assignmentResult.put("remainingOrders", pendingOrders.size());
            assignmentResult.put("assignments", assignments);

            return assignmentResult;

        } catch (Exception e) {
            System.err.println("Error in delivery assignment: " + e.getMessage());
            
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", e.getMessage());
            return errorResult;
        }
    }

    /**
     * Get real-time delivery status for all active deliveries
     */
    public List<Map<String, Object>> getActiveDeliveryStatus() {
        List<Map<String, Object>> activeDeliveries = new ArrayList<>();

        try {
            // Get all drivers currently on delivery routes
            List<Driver> activeDrivers = driverRepository.findByStatus(Driver.DriverStatus.ACTIVE);

            for (Driver driver : activeDrivers) {
                GPSTrackingService.RouteProgress progress = 
                    gpsTrackingService.getRouteProgress(driver.getDriverId());

                if (progress != null) {
                    Map<String, Object> deliveryStatus = new HashMap<>();
                    deliveryStatus.put("driverId", driver.getDriverId());
                    deliveryStatus.put("driverName", driver.getName());
                    deliveryStatus.put("vehicleNumber", driver.getVehicleNumber());
                    
                    OptimizedRoute route = progress.getRoute();
                    deliveryStatus.put("routeId", route.getRouteId());
                    deliveryStatus.put("totalDeliveries", route.getTotalDeliveryPoints());
                    deliveryStatus.put("completedDeliveries", progress.getCompletedDeliveries().size());
                    deliveryStatus.put("progressPercentage", route.getProgressPercentage());
                    
                    // Get current location
                    GPSTrackingService.DriverLocation location = 
                        gpsTrackingService.getCurrentLocation(driver.getDriverId());
                    
                    if (location != null) {
                        deliveryStatus.put("currentLocation", Map.of(
                            "latitude", location.getLatitude(),
                            "longitude", location.getLongitude(),
                            "speed", location.getSpeed(),
                            "lastUpdated", location.getTimestamp()
                        ));
                    }

                    // Get next delivery
                    RoutePoint nextDelivery = getNextDeliveryPoint(route);
                    if (nextDelivery != null) {
                        deliveryStatus.put("nextDelivery", Map.of(
                            "address", nextDelivery.getAddress(),
                            "orderReference", nextDelivery.getOrderReference()
                        ));

                        // Calculate ETA to next delivery
                        Map<String, Object> eta = gpsTrackingService.calculateETA(
                            driver.getDriverId(), nextDelivery.getAddress());
                        deliveryStatus.put("etaToNext", eta);
                    }

                    activeDeliveries.add(deliveryStatus);
                }
            }

        } catch (Exception e) {
            System.err.println("Error getting delivery status: " + e.getMessage());
        }

        return activeDeliveries;
    }

    /**
     * Complete a delivery and update all related systems
     */
    public Map<String, Object> completeDelivery(String driverId, String orderNumber, 
                                               Map<String, Object> deliveryDetails) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Find the order
            Order order = orderRepository.findByOrderNumber(orderNumber);
            if (order == null) {
                result.put("status", "ORDER_NOT_FOUND");
                result.put("message", "Order " + orderNumber + " not found");
                return result;
            }

            // Verify driver assignment
            Optional<Driver> driverOpt = driverRepository.findByDriverId(driverId);
            if (!driverOpt.isPresent()) {
                result.put("status", "DRIVER_NOT_FOUND");
                return result;
            }

            Driver driver = driverOpt.get();

            // Complete delivery in GPS tracking
            boolean trackingUpdated = gpsTrackingService.completeDelivery(driverId, orderNumber);
            
            if (!trackingUpdated) {
                result.put("status", "LOCATION_VERIFICATION_FAILED");
                result.put("message", "Driver must be at delivery location to complete delivery");
                return result;
            }

            // Update order status
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            orderRepository.save(order);

            // Update driver statistics
            driver.setCompletedDeliveriesToday(driver.getCompletedDeliveriesToday() + 1);
            driverRepository.save(driver);

            // Log delivery details
            String recipientName = (String) deliveryDetails.get("recipientName");
            String notes = (String) deliveryDetails.get("notes");
            
            result.put("status", "SUCCESS");
            result.put("message", "Delivery completed successfully");
            result.put("orderNumber", orderNumber);
            result.put("deliveredAt", order.getDeliveredAt());
            result.put("recipientName", recipientName);
            result.put("driverStats", Map.of(
                "completedToday", driver.getCompletedDeliveriesToday(),
                "totalCompleted", driver.getCompletedDeliveriesToday()
            ));

            System.out.println("Delivery completed: " + orderNumber + " by driver " + driverId);

        } catch (Exception e) {
            System.err.println("Error completing delivery: " + e.getMessage());
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * Get delivery performance analytics
     */
    public Map<String, Object> getDeliveryAnalytics(String period) {
        Map<String, Object> analytics = new HashMap<>();

        try {
            LocalDateTime startDate = getStartDateForPeriod(period);
            
            // Get delivery statistics
            List<Order> deliveredOrders = orderRepository.findByStatusAndDeliveredAtAfter(
                Order.OrderStatus.DELIVERED, startDate);

            analytics.put("totalDeliveries", deliveredOrders.size());
            analytics.put("period", period);
            analytics.put("startDate", startDate);

            // Calculate average delivery time
            double avgDeliveryHours = deliveredOrders.stream()
                .filter(order -> order.getCreatedAt() != null && order.getDeliveredAt() != null)
                .mapToLong(order -> 
                    java.time.Duration.between(order.getCreatedAt(), order.getDeliveredAt()).toHours())
                .average()
                .orElse(0.0);

            analytics.put("averageDeliveryTimeHours", Math.round(avgDeliveryHours * 100.0) / 100.0);

            // Driver performance
            List<Driver> activeDrivers = driverRepository.findByStatus(Driver.DriverStatus.ACTIVE);
            analytics.put("activeDrivers", activeDrivers.size());
            analytics.put("totalDriverCompletions", 
                activeDrivers.stream().mapToInt(Driver::getCompletedDeliveriesToday).sum());

            // Success rate
            List<Order> allOrders = orderRepository.findByCreatedAtAfter(startDate);
            double successRate = allOrders.isEmpty() ? 0.0 : 
                (double) deliveredOrders.size() / allOrders.size() * 100.0;
            analytics.put("successRatePercentage", Math.round(successRate * 100.0) / 100.0);

        } catch (Exception e) {
            System.err.println("Error generating analytics: " + e.getMessage());
            analytics.put("error", e.getMessage());
        }

        return analytics;
    }

    // Private helper methods

    private Map<String, List<Order>> groupOrdersByArea(List<Order> orders) {
        // Simple grouping by first part of address
        return orders.stream()
            .collect(Collectors.groupingBy(order -> {
                String address = order.getDeliveryAddress();
                if (address != null && address.contains(",")) {
                    return address.split(",")[0].trim();
                }
                return "OTHER";
            }));
    }

    private int calculateDriverCapacity(Driver driver) {
        // Base capacity on vehicle type and driver experience
        String vehicleNumber = driver.getVehicleNumber();
        if (vehicleNumber != null) {
            if (vehicleNumber.startsWith("TRK")) return 8; // Truck
            if (vehicleNumber.startsWith("VAN")) return 5; // Van
            if (vehicleNumber.startsWith("BIK")) return 2; // Bike
        }
        return 4; // Default capacity
    }

    private List<Order> selectOptimalOrdersForDriver(Driver driver, List<Order> availableOrders, 
                                                    int maxOrders) {
        // Simple selection - in production, use more sophisticated algorithms
        return availableOrders.stream()
            .limit(maxOrders)
            .collect(Collectors.toList());
    }

    private RoutePoint getNextDeliveryPoint(OptimizedRoute route) {
        return route.getRoutePoints().stream()
            .filter(point -> "DELIVERY".equals(point.getPointType()) && 
                           "PENDING".equals(point.getStatus()))
            .findFirst()
            .orElse(null);
    }

    private LocalDateTime getStartDateForPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        switch (period.toLowerCase()) {
            case "today": return now.toLocalDate().atStartOfDay();
            case "week": return now.minusWeeks(1);
            case "month": return now.minusMonths(1);
            default: return now.minusDays(1); // Yesterday
        }
    }
}
