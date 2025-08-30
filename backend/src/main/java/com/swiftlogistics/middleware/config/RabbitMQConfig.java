package com.swiftlogistics.middleware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for message queues
 */
@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String ORDER_PROCESSING_QUEUE = "order.processing";
    public static final String STATUS_UPDATE_QUEUE = "status.update";
    public static final String NOTIFICATION_QUEUE = "notification";
    public static final String DLQ_SUFFIX = ".dlq";
    
    // Exchange names
    public static final String DIRECT_EXCHANGE = "swift.logistics.direct";
    public static final String TOPIC_EXCHANGE = "swift.logistics.topic";
    public static final String DLX_EXCHANGE = "swift.logistics.dlx";

    // Dead Letter Exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // Main exchanges
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    // Order Processing Queue with DLQ
    @Bean
    public Queue orderProcessingQueue() {
        return QueueBuilder.durable(ORDER_PROCESSING_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", ORDER_PROCESSING_QUEUE + DLQ_SUFFIX)
                .withArgument("x-message-ttl", 300000) // 5 minutes TTL
                .build();
    }

    @Bean
    public Queue orderProcessingDLQ() {
        return QueueBuilder.durable(ORDER_PROCESSING_QUEUE + DLQ_SUFFIX).build();
    }

    // Status Update Queue
    @Bean
    public Queue statusUpdateQueue() {
        return QueueBuilder.durable(STATUS_UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", STATUS_UPDATE_QUEUE + DLQ_SUFFIX)
                .build();
    }

    @Bean
    public Queue statusUpdateDLQ() {
        return QueueBuilder.durable(STATUS_UPDATE_QUEUE + DLQ_SUFFIX).build();
    }

    // Notification Queue
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", NOTIFICATION_QUEUE + DLQ_SUFFIX)
                .build();
    }

    @Bean
    public Queue notificationDLQ() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE + DLQ_SUFFIX).build();
    }

    // Bindings
    @Bean
    public Binding orderProcessingBinding() {
        return BindingBuilder.bind(orderProcessingQueue())
                .to(directExchange())
                .with("order.process");
    }

    @Bean
    public Binding statusUpdateBinding() {
        return BindingBuilder.bind(statusUpdateQueue())
                .to(topicExchange())
                .with("status.update.*");
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(topicExchange())
                .with("notification.*");
    }

    // DLQ Bindings
    @Bean
    public Binding orderProcessingDLQBinding() {
        return BindingBuilder.bind(orderProcessingDLQ())
                .to(deadLetterExchange())
                .with(ORDER_PROCESSING_QUEUE + DLQ_SUFFIX);
    }

    @Bean
    public Binding statusUpdateDLQBinding() {
        return BindingBuilder.bind(statusUpdateDLQ())
                .to(deadLetterExchange())
                .with(STATUS_UPDATE_QUEUE + DLQ_SUFFIX);
    }

    @Bean
    public Binding notificationDLQBinding() {
        return BindingBuilder.bind(notificationDLQ())
                .to(deadLetterExchange())
                .with(NOTIFICATION_QUEUE + DLQ_SUFFIX);
    }

    // JSON Message Converter
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate with JSON converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
