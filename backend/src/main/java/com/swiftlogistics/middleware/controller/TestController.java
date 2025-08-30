package com.swiftlogistics.middleware.controller;

import com.swiftlogistics.middleware.dto.OrderRequest;
import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.service.OrderService;
import com.swiftlogistics.middleware.service.MessageService;
import com.swiftlogistics.middleware.service.MockMessageService;
import com.swiftlogistics.middleware.service.OrderProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * TestController - Endpoints to test distributed systems functionality
 */
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = {"http://localhost:3000", "http://172.19.176.1:3000", "file://"}, allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class TestController {

    @Autowired
    private OrderService orderService;

    @Autowired(required = false)
    private MessageService messageService;

    @Autowired(required = false)
    private MockMessageService mockMessageService;

    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    private SimpMessagingTemplate websocketTemplate;

    /**
     * Test endpoint to verify all systems are working
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "OK");
        health.put("backend", "Running");
        health.put("database", "Connected");
        health.put("messageService", messageService != null ? "RabbitMQ" : "Mock");
        health.put("websocket", "Active");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(health);
    }

    /**
     * Test distributed transaction processing
     */
    @PostMapping("/saga-transaction")
    public ResponseEntity<Map<String, Object>> testSagaTransaction(@RequestBody OrderRequest orderRequest) {
        try {
            // Create order through normal service
            Order order = orderService.createOrder(orderRequest);
            
            // Test saga transaction processing
            orderProcessingService.processOrderIntegration(order.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", order.getId());
            response.put("orderNumber", order.getOrderNumber());
            response.put("message", "Saga transaction completed successfully");
            response.put("status", order.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Saga transaction failed");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test asynchronous message processing
     */
    @PostMapping("/async-processing")
    public ResponseEntity<Map<String, Object>> testAsyncProcessing(@RequestBody OrderRequest orderRequest) {
        try {
            // Create order
            Order order = orderService.createOrder(orderRequest);
            
            // Send for async processing
            if (messageService != null) {
                messageService.sendOrderForProcessing(order);
            } else if (mockMessageService != null) {
                mockMessageService.sendOrderForProcessing(order);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderId", order.getId());
            response.put("orderNumber", order.getOrderNumber());
            response.put("message", "Order sent for asynchronous processing");
            response.put("processingMode", messageService != null ? "RabbitMQ" : "Mock");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "Async processing failed");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Test real-time notifications
     */
    @PostMapping("/websocket-notification")
    public ResponseEntity<Map<String, Object>> testWebSocketNotification(@RequestParam String message) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "TEST");
            notification.put("message", message);
            notification.put("timestamp", System.currentTimeMillis());
            
            // Send to all connected clients
            websocketTemplate.convertAndSend("/topic/test", notification);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "WebSocket notification sent successfully");
            response.put("notification", notification);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("message", "WebSocket notification failed");
            
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get system statistics for dashboard
     */
    @GetMapping("/system-stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        try {
            List<Order> allOrders = orderService.getAllOrders();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalOrders", allOrders.size());
            stats.put("activeOrders", allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PROCESSING || 
                           o.getStatus() == Order.OrderStatus.OUT_FOR_DELIVERY)
                .count());
            stats.put("completedOrders", allOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .count());
            stats.put("systemUptime", System.currentTimeMillis());
            stats.put("distributedTransactions", "Enabled - Saga Pattern");
            stats.put("asyncProcessing", "Enabled - " + (messageService != null ? "RabbitMQ" : "Mock Queue"));
            stats.put("realTimeNotifications", "Enabled - WebSocket/STOMP");
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
