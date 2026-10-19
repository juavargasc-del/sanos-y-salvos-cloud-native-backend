package com.sanosysalvos.mascotas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "mascotas.exchange";
    public static final String QUEUE = "mascotas.registradas.queue";
    public static final String ROUTING_KEY = "mascota.registrada";

    @Bean
    public DirectExchange mascotasExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue mascotasQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding mascotasBinding(
            Queue mascotasQueue,
            DirectExchange mascotasExchange) {

        return BindingBuilder
                .bind(mascotasQueue)
                .to(mascotasExchange)
                .with(ROUTING_KEY);
    }
}