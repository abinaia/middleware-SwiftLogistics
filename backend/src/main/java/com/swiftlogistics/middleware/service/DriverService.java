package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Driver;
import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.repository.DriverRepository;
import com.swiftlogistics.middleware.repository.OrderRepository;
import com.swiftlogistics.middleware.dto.DriverDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Service for managing driver operations
 */
@Service
@Transactional
public class DriverService {
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * Get driver dashboard data
     */
    public DriverDashboardResponse getDashboardData(String driverId) {
        Driver driver = getDriverByDriverId(driverId);
        
        DriverDashboardResponse response = new DriverDashboardResponse(driver);
        
        // Get active deliveries count (orders assigned to this driver)
        List<Order> activeOrders = orderRepository.findByStatus(Order.OrderStatus.OUT_FOR_DELIVERY);
        response.setActiveDeliveries(activeOrders.size());
        
        // Set current route information (mock for now)
        if (driver.getStatus() == Driver.DriverStatus.ACTIVE) {
            DriverDashboardResponse.CurrentRouteInfo currentRoute = new DriverDashboardResponse.CurrentRouteInfo();
            currentRoute.setRouteId("RT" + String.format("%03d", new Random().nextInt(999)));
            currentRoute.setRouteName("Route " + (char)('A' + new Random().nextInt(26)));
            currentRoute.setTotalStops(8 + new Random().nextInt(7)); // 8-14 stops
            currentRoute.setCompletedStops(new Random().nextInt(currentRoute.getTotalStops()));
            
            int remainingTime = (currentRoute.getTotalStops() - currentRoute.getCompletedStops()) * 20; // 20 min per stop
            currentRoute.setEstimatedTime(String.format("%dh %02dm", remainingTime / 60, remainingTime % 60));
            
            response.setCurrentRoute(currentRoute);
        }
        
        return response;
    }
    
    /**
     * Get driver by driver ID
     */
    public Driver getDriverByDriverId(String driverId) {
        return driverRepository.findByDriverId(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
    }
    
    /**
     * Update driver status
     */
    public Driver updateDriverStatus(String driverId, Driver.DriverStatus status) {
        Driver driver = getDriverByDriverId(driverId);
        driver.setStatus(status);
        return driverRepository.save(driver);
    }
    
    /**
     * Start driver shift
     */
    public Driver startShift(String driverId) {
        Driver driver = getDriverByDriverId(driverId);
        driver.startShift();
        return driverRepository.save(driver);
    }
    
    /**
     * End driver shift
     */
    public Driver endShift(String driverId) {
        Driver driver = getDriverByDriverId(driverId);
        driver.endShift();
        return driverRepository.save(driver);
    }
    
    /**
     * Get driver statistics
     */
    public DriverDashboardResponse getDriverStats(String driverId, LocalDate date) {
        Driver driver = getDriverByDriverId(driverId);
        
        // If specific date requested, calculate stats for that date
        if (date != null && !date.equals(LocalDate.now())) {
            // For historical data, you would query from a statistics table
            // For now, return current stats
        }
        
        return new DriverDashboardResponse(driver);
    }
    
    /**
     * Create a new driver (for testing)
     */
    public Driver createDriver(String driverId, String name, String email, String vehicleNumber) {
        if (driverRepository.existsByDriverId(driverId)) {
            throw new RuntimeException("Driver ID already exists: " + driverId);
        }
        
        if (driverRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        Driver driver = new Driver(driverId, name, email);
        driver.setVehicleNumber(vehicleNumber);
        driver.setPhone("+94" + String.format("%09d", new Random().nextInt(1000000000)));
        driver.setLicenseNumber("DL" + String.format("%08d", new Random().nextInt(100000000)));
        driver.setAverageRating(4.0 + new Random().nextDouble()); // Random rating 4.0-5.0
        
        return driverRepository.save(driver);
    }
    
    /**
     * Get all drivers
     */
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }
    
    /**
     * Get active drivers
     */
    public List<Driver> getActiveDrivers() {
        return driverRepository.findActiveDrivers();
    }
    
    /**
     * Initialize default drivers for testing
     */
    @Transactional
    public void initializeDefaultDrivers() {
        // Check if drivers already exist
        if (driverRepository.count() > 0) {
            return; // Drivers already initialized
        }
        
        // Create test drivers
        createDriver("DRV001", "John Smith", "john.smith@swiftlogistics.lk", "VAN-123");
        createDriver("DRV002", "Mary Johnson", "mary.johnson@swiftlogistics.lk", "VAN-124");
        createDriver("DRV003", "David Wilson", "david.wilson@swiftlogistics.lk", "TRUCK-001");
        createDriver("DRV004", "Sarah Brown", "sarah.brown@swiftlogistics.lk", "VAN-125");
        createDriver("DRV005", "Michael Davis", "michael.davis@swiftlogistics.lk", "BIKE-001");
        
        System.out.println("Default drivers initialized successfully!");
    }
    
    /**
     * Update driver delivery statistics
     */
    public void updateDeliveryStats(String driverId, Double distance) {
        Driver driver = getDriverByDriverId(driverId);
        driver.incrementCompletedDeliveries();
        if (distance != null && distance > 0) {
            driver.addDistance(distance);
        }
        driverRepository.save(driver);
    }
    
    /**
     * Get drivers with active shifts
     */
    public List<Driver> getDriversWithActiveShifts() {
        return driverRepository.findDriversWithActiveShifts();
    }
}
