package com.swiftlogistics.middleware.service;

import com.swiftlogistics.middleware.model.Order;
import com.swiftlogistics.middleware.model.Client;
import com.swiftlogistics.middleware.model.Package;
import com.swiftlogistics.middleware.repository.OrderRepository;
import com.swiftlogistics.middleware.repository.ClientRepository;
import com.swiftlogistics.middleware.repository.PackageRepository;
import com.swiftlogistics.middleware.dto.OrderRequest;
import com.swiftlogistics.middleware.integration.CMSIntegrationService;
import com.swiftlogistics.middleware.integration.ROSIntegrationService;
import com.swiftlogistics.middleware.integration.WMSIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PackageRepository packageRepository;

    @Autowired
    private CMSIntegrationService cmsIntegrationService;

    @Autowired
    private ROSIntegrationService rosIntegrationService;

    @Autowired
    private WMSIntegrationService wmsIntegrationService;

    @Autowired
    private MessageService messageService;

    public Order createOrder(OrderRequest orderRequest) {
        // Get client
        Client client = clientRepository.findById(orderRequest.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        // Create order
        String orderNumber = generateOrderNumber();
        Order order = new Order(orderNumber, client, orderRequest.getDeliveryAddress(), orderRequest.getRecipientName());
        order.setRecipientPhone(orderRequest.getRecipientPhone());
        order.setTrackingNumber(generateTrackingNumber());

        // Save order first
        order = orderRepository.save(order);

        // Create package if details provided
        if (orderRequest.getPackageDescription() != null) {
            Package pkg = new Package(generatePackageId(), order, orderRequest.getPackageDescription());
            pkg.setWeight(orderRequest.getPackageWeight());
            pkg.setDimensions(orderRequest.getPackageDimensions());
            packageRepository.save(pkg);
        }

        // Send to external systems asynchronously
        processOrderAsync(order);

        return order;
    }

    /**
     * Send order to message queue for async processing
     */
    private void processOrderAsync(Order order) {
        messageService.sendOrderForProcessing(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Order getOrderByTrackingNumber(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }

    public List<Order> getOrdersByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setStatus(status);
            order = orderRepository.save(order);
            
            // Send real-time notification
            messageService.sendStatusUpdate(order);
        }
        return order;
    }

    /**
     * Get orders by status
     */
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Update an existing order
     */
    public Order updateOrder(Order order) {
        order.setUpdatedAt(java.time.LocalDateTime.now());
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private String generateTrackingNumber() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generatePackageId() {
        return "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
