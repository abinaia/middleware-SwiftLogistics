package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Route;
import com.swiftlogistics.middleware.model.Delivery;
import com.swiftlogistics.middleware.repository.RouteRepository;
import com.swiftlogistics.middleware.repository.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    /**
     * Create a new route with deliveries
     */
    public Route createRoute(String routeName, String driverId, List<Map<String, Object>> deliveryData) {
        // Generate route ID
        String routeId = "RT" + System.currentTimeMillis();
        
        // Create route
        Route route = new Route(routeId, routeName, driverId);
        route.setTotalStops(deliveryData.size());
        
        // Calculate estimated time and distance
        double totalDistance = calculateTotalDistance(deliveryData);
        int estimatedTime = (int) Math.ceil(totalDistance / 40.0 * 60); // 40 km/h average
        
        route.setTotalDistanceKm(totalDistance);
        route.setEstimatedTimeMinutes(estimatedTime);
        
        // Save route first
        Route savedRoute = routeRepository.save(route);
        
        // Create deliveries
        for (int i = 0; i < deliveryData.size(); i++) {
            Map<String, Object> delivery = deliveryData.get(i);
            
            String deliveryId = "D" + System.currentTimeMillis() + "_" + i;
            String customerName = (String) delivery.get("customerName");
            String address = (String) delivery.get("address");
            String phone = (String) delivery.get("phone");
            
            Delivery newDelivery = new Delivery(deliveryId, savedRoute, customerName, address);
            newDelivery.setCustomerPhone(phone);
            newDelivery.setSequenceNumber(i + 1);
            
            // Generate coordinates from address
            newDelivery.setDeliveryLatitude(routeOptimizationService.generateLatitudeFromAddress(address));
            newDelivery.setDeliveryLongitude(routeOptimizationService.generateLongitudeFromAddress(address));
            
            deliveryRepository.save(newDelivery);
        }
        
        return savedRoute;
    }

    /**
     * Get routes for a specific driver
     */
    public List<Route> getDriverRoutes(String driverId) {
        return routeRepository.findByDriverIdOrderByCreatedAtDesc(driverId);
    }

    /**
     * Get active route for a driver
     */
    public Route getActiveRoute(String driverId) {
        List<Route> activeRoutes = routeRepository.findByDriverIdAndStatus(driverId, Route.RouteStatus.IN_PROGRESS);
        return activeRoutes.isEmpty() ? null : activeRoutes.get(0);
    }

    /**
     * Start a route
     */
    public Map<String, Object> startRoute(String routeId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Route route = routeRepository.findByRouteId(routeId);
            if (route == null) {
                result.put("success", false);
                result.put("message", "Route not found");
                return result;
            }
            
            if (route.getStatus() != Route.RouteStatus.PENDING) {
                result.put("success", false);
                result.put("message", "Route already started or completed");
                return result;
            }
            
            route.setStatus(Route.RouteStatus.IN_PROGRESS);
            route.setStartedAt(LocalDateTime.now());
            routeRepository.save(route);
            
            // Update all deliveries to IN_TRANSIT
            List<Delivery> deliveries = deliveryRepository.findByRoute(route);
            deliveries.forEach(delivery -> {
                if (delivery.getStatus() == Delivery.DeliveryStatus.PENDING) {
                    delivery.setStatus(Delivery.DeliveryStatus.IN_TRANSIT);
                    deliveryRepository.save(delivery);
                }
            });
            
            result.put("success", true);
            result.put("message", "Route started successfully");
            result.put("route", route);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error starting route: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Complete a delivery
     */
    public Map<String, Object> completeDelivery(String deliveryId, String notes, String signatureUrl) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId);
            if (delivery == null) {
                result.put("success", false);
                result.put("message", "Delivery not found");
                return result;
            }
            
            delivery.setStatus(Delivery.DeliveryStatus.COMPLETED);
            delivery.setCompletedAt(LocalDateTime.now());
            delivery.setDeliveryNotes(notes);
            delivery.setSignatureUrl(signatureUrl);
            deliveryRepository.save(delivery);
            
            // Update route progress
            Route route = delivery.getRoute();
            long completedCount = deliveryRepository.countByRouteAndStatus(route, Delivery.DeliveryStatus.COMPLETED);
            route.setCompletedStops((int) completedCount);
            
            // Check if route is complete
            if (completedCount == route.getTotalStops()) {
                route.setStatus(Route.RouteStatus.COMPLETED);
                route.setCompletedAt(LocalDateTime.now());
            }
            
            routeRepository.save(route);
            
            result.put("success", true);
            result.put("message", "Delivery completed successfully");
            result.put("delivery", delivery);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Error completing delivery: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get deliveries for a route
     */
    public List<Delivery> getRouteDeliveries(String routeId) {
        Route route = routeRepository.findByRouteId(routeId);
        if (route == null) return List.of();
        
        return deliveryRepository.findByRouteOrderBySequenceNumber(route);
    }

    /**
     * Get pending deliveries for a driver
     */
    public List<Delivery> getPendingDeliveries(String driverId) {
        return deliveryRepository.findByDriverIdAndStatus(driverId, Delivery.DeliveryStatus.IN_TRANSIT);
    }

    /**
     * Get completed deliveries for a driver
     */
    public List<Delivery> getCompletedDeliveries(String driverId) {
        return deliveryRepository.findByDriverIdAndStatus(driverId, Delivery.DeliveryStatus.COMPLETED);
    }

    // Private helper methods
    private double calculateTotalDistance(List<Map<String, Object>> deliveryData) {
        // Simple distance calculation for demo
        // In production, use real mapping service
        return deliveryData.size() * 5.0; // Average 5km per stop
    }
}
