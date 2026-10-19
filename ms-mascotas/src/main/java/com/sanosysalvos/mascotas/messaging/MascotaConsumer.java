package com.sanosysalvos.mascotas.messaging;

import com.sanosysalvos.mascotas.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MascotaConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void recibirMascotaRegistrada(String mascotaId) {

        System.out.println(
            "[RABBITMQ] Evento recibido: mascota registrada con ID "
            + mascotaId
        );

        System.out.println(
            "[RABBITMQ] Procesamiento de notificación completado"
        );
    }
}