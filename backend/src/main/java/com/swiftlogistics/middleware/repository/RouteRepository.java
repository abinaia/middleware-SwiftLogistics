package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    Route findByRouteId(String routeId);
    
    List<Route> findByDriverId(String driverId);
    
    List<Route> findByStatus(Route.RouteStatus status);
    
    List<Route> findByDriverIdAndStatus(String driverId, Route.RouteStatus status);
    
    @Query("SELECT r FROM Route r WHERE r.driverId = :driverId ORDER BY r.createdAt DESC")
    List<Route> findByDriverIdOrderByCreatedAtDesc(@Param("driverId") String driverId);
    
    @Query("SELECT r FROM Route r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    List<Route> findByStatusInOrderByCreatedAtDesc(@Param("statuses") List<Route.RouteStatus> statuses);
}
