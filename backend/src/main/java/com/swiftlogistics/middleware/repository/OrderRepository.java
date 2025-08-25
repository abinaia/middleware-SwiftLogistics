package com.swiftlogistics.middleware.repository;

import com.swiftlogistics.middleware.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Order findByTrackingNumber(String trackingNumber);
    
    List<Order> findByClientId(Long clientId);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    Order findByOrderNumber(String orderNumber);
}
