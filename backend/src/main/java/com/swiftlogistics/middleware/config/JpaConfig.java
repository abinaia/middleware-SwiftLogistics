package com.swiftlogistics.middleware.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.swiftlogistics.middleware.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration is handled through application.properties
}
