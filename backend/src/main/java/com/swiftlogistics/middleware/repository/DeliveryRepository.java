package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Delivery;
import com.swiftlogistics.middleware.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    Delivery findByDeliveryId(String deliveryId);
    
    List<Delivery> findByRoute(Route route);
    
    List<Delivery> findByRouteOrderBySequenceNumber(Route route);
    
    List<Delivery> findByStatus(Delivery.DeliveryStatus status);
    
    @Query("SELECT d FROM Delivery d WHERE d.route.driverId = :driverId AND d.status = :status ORDER BY d.sequenceNumber")
    List<Delivery> findByDriverIdAndStatus(@Param("driverId") String driverId, 
                                          @Param("status") Delivery.DeliveryStatus status);
    
    @Query("SELECT d FROM Delivery d WHERE d.route.driverId = :driverId ORDER BY d.sequenceNumber")
    List<Delivery> findByDriverIdOrderBySequence(@Param("driverId") String driverId);
    
    @Query("SELECT COUNT(d) FROM Delivery d WHERE d.route = :route AND d.status = :status")
    Long countByRouteAndStatus(@Param("route") Route route, @Param("status") Delivery.DeliveryStatus status);
}
