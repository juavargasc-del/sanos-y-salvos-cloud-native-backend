package com.sanosysalvos.mascotas.messaging;

import com.sanosysalvos.mascotas.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void publicarMascotaRegistrada(Long mascotaId) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                mascotaId.toString()
        );

        System.out.println(
                "[RABBITMQ] Evento publicado: mascota registrada con ID "
                + mascotaId
        );
    }
}