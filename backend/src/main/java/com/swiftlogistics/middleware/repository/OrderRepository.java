package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Order findByTrackingNumber(String trackingNumber);
    
    List<Order> findByClientId(Long clientId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    Order findByOrderNumber(String orderNumber);
    
    // Additional methods for delivery management
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.deliveredAt > :deliveredAt")
    List<Order> findByStatusAndDeliveredAtAfter(@Param("status") Order.OrderStatus status, 
                                               @Param("deliveredAt") LocalDateTime deliveredAt);
    
    @Query("SELECT o FROM Order o WHERE o.createdAt > :createdAt")
    List<Order> findByCreatedAtAfter(@Param("createdAt") LocalDateTime createdAt);
    
    // Find orders ready for delivery assignment
    @Query("SELECT o FROM Order o WHERE o.status IN :statuses ORDER BY o.createdAt ASC")
    List<Order> findOrdersReadyForDelivery(@Param("statuses") List<Order.OrderStatus> statuses);
}
