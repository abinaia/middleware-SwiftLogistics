package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Driver;
import com.swiftlogistics.middleware.dto.OptimizedRoute;
import com.swiftlogistics.middleware.dto.RoutePoint;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Real-time GPS tracking and location management service
 * Replaces mock location data with actual tracking capabilities
 */
@Service
public class GPSTrackingService {

    // Store real-time driver locations
    private final Map<String, DriverLocation> driverLocationCache = new ConcurrentHashMap<>();
    
    // Store route progress for each driver
    private final Map<String, RouteProgress> routeProgressCache = new ConcurrentHashMap<>();

    @Autowired
    private RouteOptimizationService routeOptimizationService;

    /**
     * Update driver's real-time location
     */
    public void updateDriverLocation(String driverId, double latitude, double longitude, 
                                   double speed, double heading, Date timestamp) {
        DriverLocation location = new DriverLocation(
            driverId, latitude, longitude, speed, heading, timestamp
        );
        
        driverLocationCache.put(driverId, location);
        
        // Update route progress if driver is on a route
        updateRouteProgress(driverId, latitude, longitude);
        
        System.out.println("Updated location for driver " + driverId + 
                          ": (" + latitude + ", " + longitude + ") at " + speed + " km/h");
    }

    /**
     * Get current location of a driver
     */
    public DriverLocation getCurrentLocation(String driverId) {
        return driverLocationCache.get(driverId);
    }

    /**
     * Get all driver locations for fleet management
     */
    public Map<String, DriverLocation> getAllDriverLocations() {
        return new HashMap<>(driverLocationCache);
    }

    /**
     * Calculate estimated arrival time based on current location and route
     */
    public Map<String, Object> calculateETA(String driverId, String destinationAddress) {
        DriverLocation currentLocation = getCurrentLocation(driverId);
        Map<String, Object> eta = new HashMap<>();
        
        if (currentLocation == null) {
            eta.put("status", "NO_LOCATION_DATA");
            eta.put("estimatedMinutes", null);
            return eta;
        }

        try {
            // Calculate distance to destination
            double destLat = routeOptimizationService.generateLatitudeFromAddress(destinationAddress);
            double destLon = routeOptimizationService.generateLongitudeFromAddress(destinationAddress);
            
            double distance = calculateDistance(
                currentLocation.getLatitude(), currentLocation.getLongitude(),
                destLat, destLon
            );

            // Calculate ETA based on current speed and traffic
            double averageSpeed = Math.max(currentLocation.getSpeed(), 25.0); // min 25 km/h
            double estimatedHours = distance / averageSpeed;
            int estimatedMinutes = (int) Math.ceil(estimatedHours * 60);

            eta.put("status", "CALCULATED");
            eta.put("estimatedMinutes", estimatedMinutes);
            eta.put("distanceKm", Math.round(distance * 100.0) / 100.0);
            eta.put("currentSpeed", currentLocation.getSpeed());
            eta.put("lastUpdated", currentLocation.getTimestamp());

        } catch (Exception e) {
            System.err.println("Error calculating ETA: " + e.getMessage());
            eta.put("status", "CALCULATION_ERROR");
            eta.put("estimatedMinutes", null);
        }

        return eta;
    }

    /**
     * Track route progress for a driver
     */
    public void startRouteTracking(String driverId, OptimizedRoute route) {
        RouteProgress progress = new RouteProgress(driverId, route);
        routeProgressCache.put(driverId, progress);
        
        System.out.println("Started route tracking for driver " + driverId + 
                          " with " + route.getTotalDeliveryPoints() + " deliveries");
    }

    /**
     * Get route progress for a driver
     */
    public RouteProgress getRouteProgress(String driverId) {
        return routeProgressCache.get(driverId);
    }

    /**
     * Mark delivery as completed at current location
     */
    public boolean completeDelivery(String driverId, String orderReference) {
        RouteProgress progress = routeProgressCache.get(driverId);
        DriverLocation currentLocation = getCurrentLocation(driverId);
        
        if (progress == null || currentLocation == null) {
            return false;
        }

        // Find the delivery point in the route
        for (RoutePoint point : progress.getRoute().getRoutePoints()) {
            if (point.getOrderReference().equals(orderReference) && 
                "DELIVERY".equals(point.getPointType())) {
                
                // Verify driver is near the delivery location
                double distance = calculateDistance(
                    currentLocation.getLatitude(), currentLocation.getLongitude(),
                    point.getLatitude(), point.getLongitude()
                );

                if (distance <= 0.5) { // Within 500 meters
                    point.setStatus("COMPLETED");
                    progress.addCompletedDelivery(orderReference, new Date());
                    
                    System.out.println("Delivery completed: " + orderReference + 
                                      " by driver " + driverId);
                    return true;
                } else {
                    System.out.println("Driver too far from delivery location: " + 
                                      distance + "km away");
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Get live tracking data for mobile app
     */
    public Map<String, Object> getLiveTrackingData(String driverId) {
        DriverLocation location = getCurrentLocation(driverId);
        RouteProgress progress = getRouteProgress(driverId);
        
        Map<String, Object> trackingData = new HashMap<>();
        
        if (location != null) {
            trackingData.put("currentLocation", Map.of(
                "latitude", location.getLatitude(),
                "longitude", location.getLongitude(),
                "speed", location.getSpeed(),
                "heading", location.getHeading(),
                "timestamp", location.getTimestamp()
            ));
        }

        if (progress != null) {
            trackingData.put("routeProgress", Map.of(
                "totalDeliveries", progress.getRoute().getTotalDeliveryPoints(),
                "completedDeliveries", progress.getCompletedDeliveries().size(),
                "nextDelivery", getNextDeliveryPoint(progress),
                "estimatedCompletion", calculateRouteCompletionETA(progress)
            ));
        }

        trackingData.put("status", "ACTIVE");
        trackingData.put("lastUpdated", new Date());
        
        return trackingData;
    }

    // Private helper methods

    private void updateRouteProgress(String driverId, double latitude, double longitude) {
        RouteProgress progress = routeProgressCache.get(driverId);
        if (progress != null) {
            progress.updateCurrentPosition(latitude, longitude);
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }

    private RoutePoint getNextDeliveryPoint(RouteProgress progress) {
        for (RoutePoint point : progress.getRoute().getRoutePoints()) {
            if ("DELIVERY".equals(point.getPointType()) && 
                "PENDING".equals(point.getStatus())) {
                return point;
            }
        }
        return null;
    }

    private Date calculateRouteCompletionETA(RouteProgress progress) {
        // Calculate based on remaining deliveries and average time per delivery
        int remaining = progress.getRoute().getTotalDeliveryPoints() - 
                       progress.getCompletedDeliveries().size();
        
        if (remaining == 0) {
            return new Date(); // Already completed
        }

        // Estimate 15 minutes per delivery + travel time
        int estimatedMinutes = remaining * 15;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, estimatedMinutes);
        
        return cal.getTime();
    }

    // Inner classes for data structures

    public static class DriverLocation {
        private String driverId;
        private double latitude;
        private double longitude;
        private double speed; // km/h
        private double heading; // degrees
        private Date timestamp;

        public DriverLocation(String driverId, double latitude, double longitude, 
                            double speed, double heading, Date timestamp) {
            this.driverId = driverId;
            this.latitude = latitude;
            this.longitude = longitude;
            this.speed = speed;
            this.heading = heading;
            this.timestamp = timestamp;
        }

        // Getters
        public String getDriverId() { return driverId; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public double getSpeed() { return speed; }
        public double getHeading() { return heading; }
        public Date getTimestamp() { return timestamp; }
    }

    public static class RouteProgress {
        private String driverId;
        private OptimizedRoute route;
        private double currentLatitude;
        private double currentLongitude;
        private Map<String, Date> completedDeliveries;
        private Date startTime;

        public RouteProgress(String driverId, OptimizedRoute route) {
            this.driverId = driverId;
            this.route = route;
            this.completedDeliveries = new HashMap<>();
            this.startTime = new Date();
        }

        public void updateCurrentPosition(double latitude, double longitude) {
            this.currentLatitude = latitude;
            this.currentLongitude = longitude;
        }

        public void addCompletedDelivery(String orderReference, Date completionTime) {
            completedDeliveries.put(orderReference, completionTime);
        }

        // Getters
        public String getDriverId() { return driverId; }
        public OptimizedRoute getRoute() { return route; }
        public double getCurrentLatitude() { return currentLatitude; }
        public double getCurrentLongitude() { return currentLongitude; }
        public Map<String, Date> getCompletedDeliveries() { return completedDeliveries; }
        public Date getStartTime() { return startTime; }
    }
}
