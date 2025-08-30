package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    // Find driver by driver ID
    Optional<Driver> findByDriverId(String driverId);
    
    // Find driver by email
    Optional<Driver> findByEmail(String email);
    
    // Find drivers by status
    List<Driver> findByStatus(Driver.DriverStatus status);
    
    // Find active drivers
    @Query("SELECT d FROM Driver d WHERE d.status = 'ACTIVE'")
    List<Driver> findActiveDrivers();
    
    // Find drivers with active shifts
    @Query("SELECT d FROM Driver d WHERE d.shiftStartTime IS NOT NULL AND d.shiftEndTime IS NULL")
    List<Driver> findDriversWithActiveShifts();
    
    // Find drivers by vehicle number
    Optional<Driver> findByVehicleNumber(String vehicleNumber);
    
    // Get driver statistics for today
    @Query("SELECT d FROM Driver d WHERE d.shiftStartTime >= :startOfDay")
    List<Driver> findDriversWithShiftsToday(@Param("startOfDay") LocalDateTime startOfDay);
    
    // Check if driver exists by driver ID
    boolean existsByDriverId(String driverId);
    
    // Check if email is already taken
    boolean existsByEmail(String email);
}
