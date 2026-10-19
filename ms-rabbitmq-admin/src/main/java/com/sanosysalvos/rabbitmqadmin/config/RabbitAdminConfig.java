package com.sanosysalvos.rabbitmqadmin.config;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RabbitAdminConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RestClient rabbitManagementClient(
            RestClient.Builder builder,
            RabbitManagementProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .defaultHeaders(headers -> headers.setBasicAuth(
                        properties.username(), properties.password()))
                .build();
    }

}
