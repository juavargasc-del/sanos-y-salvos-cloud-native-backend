package com.sanosysalvos.mascotas.messaging;

import com.sanosysalvos.mascotas.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.Header;

@Component
public class MascotaConsumer {

    private static final Logger log = LoggerFactory.getLogger(MascotaConsumer.class);
    private static final int MAX_INTENTOS = 3;
    private final boolean simularErrorProcesamiento;

    public MascotaConsumer(
            @Value("${mascotas.rabbitmq.simulate-processing-error:false}")
            boolean simularErrorProcesamiento) {
        this.simularErrorProcesamiento = simularErrorProcesamiento;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void recibirMascotaRegistrada(
            String mascotaId,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        log.info("[RABBITMQ] Mensaje recibido: {}", mascotaId);

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            log.info("[RABBITMQ] Intento {}/{}", intento, MAX_INTENTOS);
            try {
                procesarMensaje(mascotaId);
            } catch (Exception exception) {
                log.error("[RABBITMQ] Error en procesamiento (intento {}/{})",
                        intento, MAX_INTENTOS, exception);
                if (intento == MAX_INTENTOS) {
                    rechazarMensaje(channel, deliveryTag);
                }
                continue;
            }

            try {
                channel.basicAck(deliveryTag, false);
                log.info("[RABBITMQ] ACK enviado");
                return;
            } catch (Exception exception) {
                throw new IllegalStateException(
                        "No fue posible enviar el ACK del mensaje a RabbitMQ",
                        exception);
            }
        }
    }

    private void procesarMensaje(String mascotaId) {
        if (simularErrorProcesamiento) {
            throw new IllegalStateException(
                    "Error de procesamiento simulado para pruebas locales");
        }

        log.info("[RABBITMQ] Procesamiento de notificación completado para ID {}",
                mascotaId);
    }

    private void rechazarMensaje(Channel channel, long deliveryTag) {
        try {
            channel.basicNack(deliveryTag, false, false);
            log.warn("[RABBITMQ] NACK enviado - mensaje dirigido a DLQ");
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "No fue posible enviar el NACK del mensaje a RabbitMQ",
                    exception);
        }
    }
}