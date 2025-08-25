package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.service.OrderService;
import com.swiftlogistics.middleware.dto.OrderRequest;
import com.swiftlogistics.middleware.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006"})
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    // Temporary in-memory storage for demo purposes
    private static List<Map<String, Object>> ordersList = new ArrayList<>();
    private static Long nextOrderId = 5L;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Create mock order for demonstration
        String orderNumber = "ORD-" + System.currentTimeMillis();
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        Map<String, Object> newOrder = createMockOrder(
            nextOrderId++, 
            orderNumber, 
            trackingNumber, 
            orderRequest.getRecipientName(), 
            orderRequest.getDeliveryAddress(), 
            "PROCESSING"
        );
        
        // Add to our mock storage
        ordersList.add(newOrder);
        
        return ResponseEntity.ok(newOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long orderId) {
        // Find order in our mock data or newly created orders
        Map<String, Object> order = findOrderById(orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Map<String, Object>>> getOrdersByClient(@PathVariable Long clientId) {
        // Return only the dynamically created orders stored in memory
        return ResponseEntity.ok(ordersList);
    }

    private Map<String, Object> createMockOrder(Long id, String orderNumber, String trackingNumber, 
                                              String recipientName, String deliveryAddress, String status) {
        Map<String, Object> order = new HashMap<>();
        order.put("id", id);
        order.put("orderNumber", orderNumber);
        order.put("trackingNumber", trackingNumber);
        order.put("recipientName", recipientName);
        order.put("deliveryAddress", deliveryAddress);
        order.put("status", status);
        order.put("createdAt", "2025-08-23T10:00:00");
        order.put("updatedAt", "2025-08-25T19:37:46");
        
        if ("DELIVERED".equals(status)) {
            order.put("deliveredAt", "2025-08-24T19:37:46");
        }
        
        return order;
    }

    private Map<String, Object> findOrderById(Long orderId) {
        // Check only dynamically created orders
        return ordersList.stream()
            .filter(order -> orderId.equals(order.get("id")))
            .findFirst()
            .orElse(null);
    }

    private Map<String, Object> findOrderByTracking(String trackingNumber) {
        // Check only dynamically created orders
        return ordersList.stream()
            .filter(order -> trackingNumber.equals(order.get("trackingNumber")))
            .findFirst()
            .orElse(null);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Map<String, Object>> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        // Find order by tracking number
        Map<String, Object> order = findOrderByTracking(trackingNumber);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        Map<String, Object> order = findOrderById(orderId);
        if (order != null) {
            order.put("status", status);
            order.put("updatedAt", java.time.LocalDateTime.now().toString());
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() {
        // Return only dynamically created orders
        return ResponseEntity.ok(ordersList);
    }

    @GetMapping("/debug/all")
    public ResponseEntity<Map<String, Object>> getDebugInfo() {
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("totalOrders", ordersList.size());
        debugInfo.put("nextOrderId", nextOrderId);
        debugInfo.put("orders", ordersList);
        debugInfo.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(debugInfo);
    }
}
