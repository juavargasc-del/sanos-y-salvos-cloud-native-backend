package com.sanosysalvos.mascotas.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class RabbitMQConfigTest {

    private final RabbitMQConfig config = new RabbitMQConfig();

    @Test
    void mascotasQueue_debeEnviarMensajesRechazadosAlDlx() {
        var queue = config.mascotasQueue();

        assertTrue(queue.isDurable());
        assertEquals(RabbitMQConfig.DLX,
                queue.getArguments().get("x-dead-letter-exchange"));
        assertEquals(RabbitMQConfig.DLQ_ROUTING_KEY,
                queue.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void configuracion_debeCrearDlxDlqYBinding() {
        var exchange = config.mascotasDeadLetterExchange();
        var queue = config.mascotasDeadLetterQueue();
        var binding = config.mascotasDeadLetterBinding(queue, exchange);

        assertEquals(RabbitMQConfig.DLX, exchange.getName());
        assertEquals(RabbitMQConfig.DLQ, queue.getName());
        assertEquals(RabbitMQConfig.DLQ_ROUTING_KEY, binding.getRoutingKey());
    }

    @Test
    void listenerContainerFactory_debeUsarAckManual() {
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

        SimpleRabbitListenerContainerFactory factory =
                config.rabbitListenerContainerFactory(connectionFactory);

        assertEquals(AcknowledgeMode.MANUAL,
                factory.createListenerContainer().getAcknowledgeMode());
    }
}
