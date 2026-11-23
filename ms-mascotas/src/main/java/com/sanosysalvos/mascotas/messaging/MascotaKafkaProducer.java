package com.sanosysalvos.mascotas.messaging;

import com.sanosysalvos.mascotas.dto.MascotaEventoDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaKafkaProducer {

    public static final String TOPIC = "mascotas-eventos";

    private static final Logger log = LoggerFactory.getLogger(MascotaKafkaProducer.class);
    private final KafkaTemplate<String, MascotaEventoDTO> kafkaTemplate;

    /**
     * Publicacion asincronica: el REST y RabbitMQ no esperan la confirmacion Kafka.
     * Una respuesta REST exitosa significa que la entidad fue persistida y RabbitMQ fue
     * invocado, no que Kafka haya confirmado el envio. Los errores Kafka se registran
     * mediante el callback y no existe aqui un outbox ni reintento transaccional completo.
     */
    public void publicar(MascotaEventoDTO evento) {
        kafkaTemplate.send(TOPIC, evento.getMascotaId().toString(), evento)
                .whenComplete((SendResult<String, MascotaEventoDTO> resultado, Throwable error) -> {
                    if (error != null) {
                        log.error(
                                "[KAFKA] Error publicando evento tipo={} topic={} key={} ",
                                evento.getTipoEvento(),
                                TOPIC,
                                evento.getMascotaId(),
                                error);
                        return;
                    }

                    log.info(
                            "[KAFKA] Evento confirmado tipo={} topic={} key={} partition={} offset={}",
                            evento.getTipoEvento(),
                            TOPIC,
                            evento.getMascotaId(),
                            resultado.getRecordMetadata().partition(),
                            resultado.getRecordMetadata().offset());
                });
    }
}
