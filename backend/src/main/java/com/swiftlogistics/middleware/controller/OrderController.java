package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.model.Client;
import com.swiftlogistics.middleware.service.OrderService;
import com.swiftlogistics.middleware.repository.OrderRepository;
import com.swiftlogistics.middleware.repository.ClientRepository;
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
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:19006", "http://172.19.176.1:3000"})
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ClientRepository clientRepository;

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Create new order entity
        Order order = new Order();
        order.setOrderNumber("ORD-" + System.currentTimeMillis());
        order.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setRecipientName(orderRequest.getRecipientName());
        order.setRecipientPhone(orderRequest.getRecipientPhone());
        order.setStatus(Order.OrderStatus.PROCESSING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // Find or create client (for demo, we'll find by ID or create a default one)
        Client client = clientRepository.findById(orderRequest.getClientId())
            .orElseGet(() -> {
                Client newClient = new Client();
                newClient.setId(orderRequest.getClientId());
                newClient.setName("Default Client");
                newClient.setEmail("client@example.com");
                newClient.setPhoneNumber("0771234567");
                newClient.setAddress("Default Address");
                newClient.setCreatedAt(LocalDateTime.now());
                return clientRepository.save(newClient);
            });
        
        order.setClient(client);
        
        // Save to database
        Order savedOrder = orderRepository.save(order);
        
        return ResponseEntity.ok(savedOrder);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        // Find order in database
        return orderRepository.findById(orderId)
            .map(order -> ResponseEntity.ok(order))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Order>> getOrdersByClient(@PathVariable Long clientId) {
        // Get orders from database for the specific client
        List<Order> orders = orderRepository.findByClientId(clientId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<Order> getOrderByTrackingNumber(@PathVariable String trackingNumber) {
        // Find order by tracking number in database
        Order order = orderRepository.findByTrackingNumber(trackingNumber);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return orderRepository.findById(orderId)
            .map(order -> {
                order.setStatus(Order.OrderStatus.valueOf(status));
                order.setUpdatedAt(LocalDateTime.now());
                Order updatedOrder = orderRepository.save(order);
                return ResponseEntity.ok(updatedOrder);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        // Return all orders from database
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/debug/all")
    public ResponseEntity<Map<String, Object>> getDebugInfo() {
        List<Order> orders = orderRepository.findAll();
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("totalOrders", orders.size());
        debugInfo.put("orders", orders);
        debugInfo.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(debugInfo);
    }
}
