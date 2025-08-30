package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.repository.OrderRepository;
import com.swiftlogistics.middleware.integration.CMSIntegrationService;
import com.swiftlogistics.middleware.integration.ROSIntegrationService;
import com.swiftlogistics.middleware.integration.WMSIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * OrderProcessingService - Implements Saga pattern for distributed transactions
 */
@Service
public class OrderProcessingService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CMSIntegrationService cmsIntegrationService;

    @Autowired
    private ROSIntegrationService rosIntegrationService;

    @Autowired
    private WMSIntegrationService wmsIntegrationService;

    @Autowired
    @Lazy
    private MessageService messageService;

    /**
     * Process order integration using Saga pattern
     * This method implements distributed transaction management
     */
    @Transactional
    public void processOrderIntegration(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            System.err.println("Order not found: " + orderId);
            return;
        }

        SagaTransaction saga = new SagaTransaction(order);
        
        try {
            // Step 1: Submit to CMS (Client Management System)
            saga.executeStep("CMS_SUBMIT", () -> {
                cmsIntegrationService.submitOrder(order);
                updateOrderStatus(order, Order.OrderStatus.PROCESSING);
                return "CMS submission successful";
            });

            // Step 2: Add to WMS (Warehouse Management System)
            saga.executeStep("WMS_ADD", () -> {
                wmsIntegrationService.addPackageToWarehouse(order);
                updateOrderStatus(order, Order.OrderStatus.IN_WAREHOUSE);
                return "WMS package added";
            });

            // Step 3: Plan route with ROS (Route Optimization System)
            saga.executeStep("ROS_PLAN", () -> {
                rosIntegrationService.planRoute(order);
                updateOrderStatus(order, Order.OrderStatus.ROUTE_PLANNED);
                return "Route planned successfully";
            });

            // If all steps successful, mark as completed
            saga.markCompleted();
            System.out.println("Order processing completed successfully: " + order.getOrderNumber());

        } catch (Exception e) {
            // Execute compensation (rollback) actions
            handleOrderProcessingFailure(saga, e);
        }
    }

    /**
     * Synchronous order processing fallback
     */
    public void processOrderSync(Order order) {
        try {
            System.out.println("Processing order synchronously: " + order.getOrderNumber());
            processOrderIntegration(order.getId());
        } catch (Exception e) {
            System.err.println("Synchronous order processing failed: " + e.getMessage());
        }
    }

    /**
     * Handle order processing failure using compensation pattern
     */
    private void handleOrderProcessingFailure(SagaTransaction saga, Exception error) {
        System.err.println("Order processing failed, executing compensation actions: " + error.getMessage());
        
        Order order = saga.getOrder();
        
        try {
            // Execute compensation actions in reverse order
            for (String step : saga.getCompletedSteps()) {
                switch (step) {
                    case "ROS_PLAN":
                        compensateROSPlanning(order);
                        break;
                    case "WMS_ADD":
                        compensateWMSAddition(order);
                        break;
                    case "CMS_SUBMIT":
                        compensateCMSSubmission(order);
                        break;
                }
            }
            
            // Mark order as failed
            updateOrderStatus(order, Order.OrderStatus.FAILED);
            
            // Send failure notification
            messageService.sendNotification(
                order.getClient().getId(),
                "Order Processing Failed",
                "Order " + order.getOrderNumber() + " failed to process: " + error.getMessage(),
                "ERROR"
            );
            
        } catch (Exception compensationError) {
            System.err.println("Compensation failed: " + compensationError.getMessage());
            // Log to manual intervention queue
            logManualIntervention(order, error, compensationError);
        }
    }

    /**
     * Compensation actions for each service
     */
    private void compensateROSPlanning(Order order) {
        try {
            // Cancel route planning
            System.out.println("Compensating ROS planning for order: " + order.getOrderNumber());
            // In real implementation, call ROS API to cancel route
        } catch (Exception e) {
            System.err.println("ROS compensation failed: " + e.getMessage());
        }
    }

    private void compensateWMSAddition(Order order) {
        try {
            // Remove package from warehouse
            System.out.println("Compensating WMS addition for order: " + order.getOrderNumber());
            // In real implementation, call WMS API to remove package
        } catch (Exception e) {
            System.err.println("WMS compensation failed: " + e.getMessage());
        }
    }

    private void compensateCMSSubmission(Order order) {
        try {
            // Cancel order in CMS
            System.out.println("Compensating CMS submission for order: " + order.getOrderNumber());
            // In real implementation, call CMS API to cancel order
        } catch (Exception e) {
            System.err.println("CMS compensation failed: " + e.getMessage());
        }
    }

    /**
     * Update order status and send notifications
     */
    private void updateOrderStatus(Order order, Order.OrderStatus status) {
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);
        
        // Send real-time notification
        messageService.sendStatusUpdate(order);
    }

    /**
     * Log orders that require manual intervention
     */
    private void logManualIntervention(Order order, Exception originalError, Exception compensationError) {
        System.err.println("=== MANUAL INTERVENTION REQUIRED ===");
        System.err.println("Order: " + order.getOrderNumber());
        System.err.println("Original Error: " + originalError.getMessage());
        System.err.println("Compensation Error: " + compensationError.getMessage());
        System.err.println("Status: Requires manual review and correction");
        
        // In production, this would log to a special database table or alert system
        // Could also send urgent notifications to administrators
    }

    /**
     * Async method for high-volume processing
     */
    @Async
    public void processOrderAsync(Order order) {
        try {
            Thread.sleep(100); // Simulate some processing time
            processOrderIntegration(order.getId());
        } catch (Exception e) {
            System.err.println("Async order processing failed: " + e.getMessage());
        }
    }

    /**
     * Inner class to manage Saga transaction state
     */
    private static class SagaTransaction {
        private final Order order;
        private final Map<String, String> completedSteps;
        private boolean completed;

        public SagaTransaction(Order order) {
            this.order = order;
            this.completedSteps = new HashMap<>();
            this.completed = false;
        }

        public void executeStep(String stepName, SagaStep step) throws Exception {
            try {
                String result = step.execute();
                completedSteps.put(stepName, result);
                System.out.println("Saga step completed: " + stepName + " - " + result);
            } catch (Exception e) {
                System.err.println("Saga step failed: " + stepName + " - " + e.getMessage());
                throw e;
            }
        }

        public void markCompleted() {
            this.completed = true;
        }

        public Order getOrder() {
            return order;
        }

        public Iterable<String> getCompletedSteps() {
            // Return steps in reverse order for compensation
            return completedSteps.keySet().stream()
                    .sorted((a, b) -> b.compareTo(a))
                    .toList();
        }

        public boolean isCompleted() {
            return completed;
        }
    }

    /**
     * Functional interface for saga steps
     */
    @FunctionalInterface
    private interface SagaStep {
        String execute() throws Exception;
    }
}
