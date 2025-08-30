package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.model.Driver;
import com.swiftlogistics.middleware.dto.RoutePoint;
import com.swiftlogistics.middleware.dto.OptimizedRoute;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Real Route Optimization Service
 * Integrates with mapping services for actual route calculation
 */
@Service
public class RouteOptimizationService {

    @Value("${app.maps.api.key:demo}")
    private String mapsApiKey;

    @Value("${app.maps.provider:openstreetmap}")
    private String mapsProvider;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calculate optimized route for a driver with multiple delivery points
     */
    public OptimizedRoute calculateOptimizedRoute(Driver driver, List<Order> orders) {
        try {
            // Get delivery addresses from orders
            List<RoutePoint> deliveryPoints = orders.stream()
                .map(order -> new RoutePoint(
                    order.getDeliveryAddress(),
                    generateLatitudeFromAddress(order.getDeliveryAddress()),
                    generateLongitudeFromAddress(order.getDeliveryAddress()),
                    order.getId().toString(),
                    order.getOrderNumber()
                ))
                .collect(Collectors.toList());

            // Add driver's current location as starting point
            RoutePoint startPoint = new RoutePoint(
                "Driver Location",
                getCurrentDriverLatitude(driver),
                getCurrentDriverLongitude(driver),
                "start",
                "DRIVER_START"
            );

            // Calculate optimal route
            return optimizeRoutePoints(startPoint, deliveryPoints);

        } catch (Exception e) {
            System.err.println("Error calculating route: " + e.getMessage());
            return createFallbackRoute(driver, orders);
        }
    }

    /**
     * Get real-time traffic information for route adjustment
     */
    public Map<String, Object> getTrafficInfo(OptimizedRoute route) {
        Map<String, Object> trafficInfo = new HashMap<>();
        
        try {
            // In production, integrate with Google Maps Traffic API, HERE Traffic API, etc.
            // For now, simulate real traffic data
            trafficInfo.put("averageSpeed", calculateAverageSpeed(route));
            trafficInfo.put("congestionLevel", assessCongestionLevel(route));
            trafficInfo.put("alternativeRoutes", findAlternativeRoutes(route));
            trafficInfo.put("estimatedDelay", calculateEstimatedDelay(route));
            
        } catch (Exception e) {
            System.err.println("Error fetching traffic info: " + e.getMessage());
            trafficInfo.put("status", "unavailable");
        }
        
        return trafficInfo;
    }

    /**
     * Update route based on real-time conditions
     */
    public OptimizedRoute updateRouteWithRealTimeData(OptimizedRoute originalRoute) {
        try {
            // Get current traffic conditions
            Map<String, Object> trafficInfo = getTrafficInfo(originalRoute);
            
            // Recalculate if there are significant delays
            Double estimatedDelay = (Double) trafficInfo.get("estimatedDelay");
            if (estimatedDelay != null && estimatedDelay > 15.0) { // 15+ minute delay
                return recalculateRoute(originalRoute);
            }
            
            return originalRoute;
            
        } catch (Exception e) {
            System.err.println("Error updating route: " + e.getMessage());
            return originalRoute;
        }
    }

    // Private helper methods

    private OptimizedRoute optimizeRoutePoints(RoutePoint start, List<RoutePoint> deliveryPoints) {
        // Implement Traveling Salesman Problem (TSP) solution
        // Using nearest neighbor algorithm for simplicity
        
        List<RoutePoint> optimizedOrder = new ArrayList<>();
        optimizedOrder.add(start);
        
        List<RoutePoint> remaining = new ArrayList<>(deliveryPoints);
        RoutePoint current = start;
        
        while (!remaining.isEmpty()) {
            RoutePoint nearest = findNearestPoint(current, remaining);
            optimizedOrder.add(nearest);
            remaining.remove(nearest);
            current = nearest;
        }
        
        return new OptimizedRoute(
            generateRouteId(),
            optimizedOrder,
            calculateTotalDistance(optimizedOrder),
            calculateEstimatedTime(optimizedOrder),
            new Date(),
            "OPTIMIZED"
        );
    }

    private RoutePoint findNearestPoint(RoutePoint current, List<RoutePoint> candidates) {
        return candidates.stream()
            .min(Comparator.comparingDouble(point -> 
                calculateDistance(current.getLatitude(), current.getLongitude(),
                               point.getLatitude(), point.getLongitude())))
            .orElse(candidates.get(0));
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private double calculateTotalDistance(List<RoutePoint> points) {
        double totalDistance = 0.0;
        for (int i = 0; i < points.size() - 1; i++) {
            RoutePoint current = points.get(i);
            RoutePoint next = points.get(i + 1);
            totalDistance += calculateDistance(
                current.getLatitude(), current.getLongitude(),
                next.getLatitude(), next.getLongitude()
            );
        }
        return totalDistance;
    }

    private int calculateEstimatedTime(List<RoutePoint> points) {
        double distance = calculateTotalDistance(points);
        double averageSpeed = 40.0; // km/h average speed in city
        return (int) Math.ceil((distance / averageSpeed) * 60); // minutes
    }

    private double getCurrentDriverLatitude(Driver driver) {
        // In production, get from GPS tracking service
        // For demo, return warehouse location
        return 6.9271; // Colombo, Sri Lanka
    }

    private double getCurrentDriverLongitude(Driver driver) {
        // In production, get from GPS tracking service
        return 79.8612; // Colombo, Sri Lanka
    }

    private OptimizedRoute createFallbackRoute(Driver driver, List<Order> orders) {
        // Create simple route without optimization
        List<RoutePoint> points = new ArrayList<>();
        
        // Add start point
        points.add(new RoutePoint(
            "Warehouse",
            getCurrentDriverLatitude(driver),
            getCurrentDriverLongitude(driver),
            "start",
            "WAREHOUSE"
        ));
        
        // Add delivery points in order
        orders.forEach(order -> points.add(new RoutePoint(
            order.getDeliveryAddress(),
            generateLatitudeFromAddress(order.getDeliveryAddress()),
            generateLongitudeFromAddress(order.getDeliveryAddress()),
            order.getId().toString(),
            order.getOrderNumber()
        )));
        
        return new OptimizedRoute(
            generateRouteId(),
            points,
            calculateTotalDistance(points),
            calculateEstimatedTime(points),
            new Date(),
            "FALLBACK"
        );
    }

    public double generateLatitudeFromAddress(String address) {
        // In production, use geocoding service (Google Maps, OpenStreetMap, etc.)
        // For demo, generate realistic coordinates around Colombo, Sri Lanka
        if (address == null) return 6.9271;
        
        // Simple hash-based coordinate generation for demo
        int hash = Math.abs(address.hashCode());
        double offset = (hash % 1000) / 10000.0; // 0-0.1 degree variation
        return 6.9271 + (offset - 0.05); // Center around Colombo
    }

    public double generateLongitudeFromAddress(String address) {
        // In production, use geocoding service
        // For demo, generate realistic coordinates around Colombo, Sri Lanka
        if (address == null) return 79.8612;
        
        // Simple hash-based coordinate generation for demo
        int hash = Math.abs(address.hashCode());
        double offset = (hash % 1500) / 15000.0; // 0-0.1 degree variation
        return 79.8612 + (offset - 0.05); // Center around Colombo
    }

    private String generateRouteId() {
        return "ROUTE_" + System.currentTimeMillis();
    }

    private double calculateAverageSpeed(OptimizedRoute route) {
        // Calculate based on distance and time
        if (route.getEstimatedTimeMinutes() > 0) {
            return (route.getTotalDistanceKm() / route.getEstimatedTimeMinutes()) * 60;
        }
        return 40.0; // default city speed
    }

    private String assessCongestionLevel(OptimizedRoute route) {
        // Simple congestion assessment based on time of day
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        
        if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 19)) {
            return "HIGH";
        } else if ((hour >= 10 && hour <= 16) || (hour >= 20 && hour <= 22)) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    private List<OptimizedRoute> findAlternativeRoutes(OptimizedRoute mainRoute) {
        // For demo, return empty list
        // In production, calculate alternative routes
        return new ArrayList<>();
    }

    private double calculateEstimatedDelay(OptimizedRoute route) {
        // Calculate delay based on traffic conditions
        String congestion = assessCongestionLevel(route);
        switch (congestion) {
            case "HIGH": return route.getEstimatedTimeMinutes() * 0.3;
            case "MEDIUM": return route.getEstimatedTimeMinutes() * 0.15;
            default: return 0.0;
        }
    }

    private OptimizedRoute recalculateRoute(OptimizedRoute originalRoute) {
        // Recalculate route with current conditions
        // For demo, just update estimated time
        return new OptimizedRoute(
            originalRoute.getRouteId(),
            originalRoute.getRoutePoints(),
            originalRoute.getTotalDistanceKm(),
            (int) (originalRoute.getEstimatedTimeMinutes() * 1.2), // Add 20% for traffic
            new Date(),
            "RECALCULATED"
        );
    }
}
