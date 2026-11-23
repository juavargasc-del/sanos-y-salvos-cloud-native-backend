package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaEventoDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MascotaEventoConsumer {

    public static final String TOPIC = "mascotas-eventos";

    private static final Logger log =
            LoggerFactory.getLogger(MascotaEventoConsumer.class);
    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("MASCOTA_REGISTRADA", "MASCOTA_ACTUALIZADA");
    private static final Set<String> ESTADOS_PERMITIDOS =
            Set.of("PERDIDA", "ENCONTRADA");

    @KafkaListener(
            topics = TOPIC,
            groupId = "sanosysalvos-coincidencias")
    public void recibir(ConsumerRecord<String, MascotaEventoDTO> record) {
        MascotaEventoDTO evento = record.value();

        validarEvento(record, evento);

        log.info(
                "[KAFKA] Evento recibido eventId={} tipoEvento={} mascotaId={} estado={} usuarioId={} "
                        + "topic={} partition={} offset={} key={}",
                evento.getEventId(),
                evento.getTipoEvento(),
                evento.getMascotaId(),
                evento.getEstado(),
                evento.getUsuarioId(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.key());
    }

    void validarEvento(ConsumerRecord<String, MascotaEventoDTO> record,
                       MascotaEventoDTO evento) {
        if (evento == null) {
            throw new IllegalArgumentException("Evento Kafka nulo");
        }
        if (evento.getEventId() == null) {
            throw new IllegalArgumentException("Evento sin eventId");
        }
        if (evento.getMascotaId() == null) {
            throw new IllegalArgumentException("Evento sin mascotaId");
        }
        if (!TIPOS_PERMITIDOS.contains(evento.getTipoEvento())) {
            throw new IllegalArgumentException(
                    "tipoEvento no permitido: " + evento.getTipoEvento());
        }
        if (!ESTADOS_PERMITIDOS.contains(evento.getEstado())) {
            throw new IllegalArgumentException(
                    "estado no permitido: " + evento.getEstado());
        }
        if (record.key() == null
                || !record.key().equals(evento.getMascotaId().toString())) {
            throw new IllegalArgumentException(
                    "La clave del mensaje no coincide con mascotaId");
        }
    }
}
