package com.sanosysalvos.mascotas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "mascotas.exchange";
    public static final String QUEUE = "mascotas.registradas.queue";
    public static final String ROUTING_KEY = "mascota.registrada";
    public static final String DLX = "mascotas.dlx";
    public static final String DLQ = "mascotas.registradas.dlq";
    public static final String DLQ_ROUTING_KEY = "mascota.registrada.error";

    @Bean
    public DirectExchange mascotasExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue mascotasQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", DLX)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
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

    @Bean
    public DirectExchange mascotasDeadLetterExchange() {
        return new DirectExchange(DLX);
    }

    @Bean
    public Queue mascotasDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding mascotasDeadLetterBinding(
            Queue mascotasDeadLetterQueue,
            DirectExchange mascotasDeadLetterExchange) {

        return BindingBuilder
                .bind(mascotasDeadLetterQueue)
                .to(mascotasDeadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}