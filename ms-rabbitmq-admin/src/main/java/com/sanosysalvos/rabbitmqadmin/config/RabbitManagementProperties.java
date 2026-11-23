package com.sanosysalvos.rabbitmqadmin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rabbitmq.management")
public record RabbitManagementProperties(
        String baseUrl,
        String username,
        String password) {
}
