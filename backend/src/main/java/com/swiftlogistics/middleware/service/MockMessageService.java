package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.dto.OrderStatusUpdate;
import com.swiftlogistics.middleware.dto.NotificationMessage;
import com.swiftlogistics.middleware.config.MockRabbitMQConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback MessageService when RabbitMQ is not available
 */
@Service
@Profile("demo")
public class MockMessageService {
    
    @Autowired
    private SimpMessagingTemplate websocketTemplate;
    
    @Autowired
    @Lazy
    private OrderProcessingService orderProcessingService;
    
    @Autowired
    private MockRabbitMQConfig.MockMessageQueue mockOrderQueue;
    
    /**
     * Send order for asynchronous processing
     */
    public void sendOrderForProcessing(Order order) {
        try {
            Map<String, Object> orderMessage = new HashMap<>();
            orderMessage.put("orderId", order.getId());
            orderMessage.put("orderNumber", order.getOrderNumber());
            orderMessage.put("clientId", order.getClient().getId());
            orderMessage.put("timestamp", LocalDateTime.now());
            
            mockOrderQueue.send(orderMessage);
            
            // Process asynchronously
            processOrderAsync(orderMessage);
            
            System.out.println("Order sent for processing (mock): " + order.getOrderNumber());
        } catch (Exception e) {
            System.err.println("Failed to send order for processing: " + e.getMessage());
            // Fallback to synchronous processing
            orderProcessingService.processOrderSync(order);
        }
    }
    
    /**
     * Send status update notification
     */
    public void sendStatusUpdate(Order order) {
        try {
            OrderStatusUpdate statusUpdate = new OrderStatusUpdate();
            statusUpdate.setOrderId(order.getId());
            statusUpdate.setOrderNumber(order.getOrderNumber());
            statusUpdate.setStatus(order.getStatus().toString());
            statusUpdate.setUpdatedAt(LocalDateTime.now());
            statusUpdate.setClientId(order.getClient().getId());
            
            // Send real-time notification via WebSocket
            websocketTemplate.convertAndSend(
                "/topic/orders/" + order.getId(),
                statusUpdate
            );
            
            // Send client notification
            websocketTemplate.convertAndSendToUser(
                order.getClient().getId().toString(),
                "/queue/notifications",
                statusUpdate
            );
            
            System.out.println("Status update sent (mock): " + order.getOrderNumber() + " -> " + order.getStatus());
        } catch (Exception e) {
            System.err.println("Failed to send status update: " + e.getMessage());
        }
    }
    
    /**
     * Send general notification
     */
    public void sendNotification(Long clientId, String title, String message, String type) {
        try {
            NotificationMessage notification = new NotificationMessage();
            notification.setClientId(clientId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setTimestamp(LocalDateTime.now());
            
            // Send real-time notification via WebSocket
            websocketTemplate.convertAndSendToUser(
                clientId.toString(),
                "/queue/notifications",
                notification
            );
            
            System.out.println("Notification sent (mock) to client " + clientId + ": " + title);
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
    
    /**
     * Process order messages asynchronously
     */
    @Async("orderProcessingExecutor")
    public void processOrderAsync(Map<String, Object> orderMessage) {
        try {
            // Simulate some delay for async processing
            Thread.sleep(2000);
            
            Long orderId = Long.valueOf(orderMessage.get("orderId").toString());
            String orderNumber = orderMessage.get("orderNumber").toString();
            
            System.out.println("Processing order asynchronously (mock): " + orderNumber);
            
            // Process the order through integration services
            orderProcessingService.processOrderIntegration(orderId);
            
            System.out.println("Order processed successfully (mock): " + orderNumber);
        } catch (Exception e) {
            System.err.println("Failed to process order message: " + e.getMessage());
        }
    }
    
    /**
     * Send high-priority delivery notification to drivers
     */
    @Async("notificationExecutor")
    public void sendPriorityDeliveryAlert(Order order, String driverChannel) {
        try {
            Map<String, Object> alert = new HashMap<>();
            alert.put("type", "PRIORITY_DELIVERY");
            alert.put("orderId", order.getId());
            alert.put("orderNumber", order.getOrderNumber());
            alert.put("deliveryAddress", order.getDeliveryAddress());
            alert.put("recipientName", order.getRecipientName());
            alert.put("timestamp", LocalDateTime.now());
            
            websocketTemplate.convertAndSend("/topic/drivers/" + driverChannel, alert);
            
            System.out.println("Priority delivery alert sent (mock) for order: " + order.getOrderNumber());
        } catch (Exception e) {
            System.err.println("Failed to send priority delivery alert: " + e.getMessage());
        }
    }
}
