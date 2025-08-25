package com.swiftlogistics.middleware.config;

// Temporarily disabled RabbitMQ configuration due to connection issues

/*
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// RabbitMQ configuration for message queues - DISABLED
@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String ORDER_PROCESSING_QUEUE = "order.processing";
    public static final String STATUS_UPDATE_QUEUE = "status.update";
    public static final String NOTIFICATION_QUEUE = "notification";
    
    // Exchange names
    public static final String DIRECT_EXCHANGE = "swift.logistics.direct";
    public static final String TOPIC_EXCHANGE = "swift.logistics.topic";

    // All RabbitMQ beans disabled
}
*/
