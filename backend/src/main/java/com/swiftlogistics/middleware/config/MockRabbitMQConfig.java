package com.swiftlogistics.middleware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Mock RabbitMQ configuration for demo when RabbitMQ is not available
 */
@Configuration
@Profile("demo")
public class MockRabbitMQConfig {

    @Bean
    public MockMessageQueue mockOrderQueue() {
        return new MockMessageQueue("order.processing");
    }

    @Bean
    public MockMessageQueue mockStatusQueue() {
        return new MockMessageQueue("status.update");
    }

    @Bean
    public MockMessageQueue mockNotificationQueue() {
        return new MockMessageQueue("notification");
    }

    /**
     * Simple in-memory queue implementation
     */
    public static class MockMessageQueue {
        private final String queueName;
        private final ConcurrentLinkedQueue<Object> messages;
        private final ScheduledExecutorService scheduler;

        public MockMessageQueue(String queueName) {
            this.queueName = queueName;
            this.messages = new ConcurrentLinkedQueue<>();
            this.scheduler = Executors.newSingleThreadScheduledExecutor();
            startProcessing();
        }

        public void send(Object message) {
            messages.offer(message);
            System.out.println("Mock Queue [" + queueName + "] received message: " + message);
        }

        private void startProcessing() {
            scheduler.scheduleWithFixedDelay(() -> {
                Object message = messages.poll();
                if (message != null) {
                    System.out.println("Mock Queue [" + queueName + "] processing: " + message);
                    // Simulate processing time
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        }

        public String getQueueName() {
            return queueName;
        }

        public int size() {
            return messages.size();
        }
    }
}
