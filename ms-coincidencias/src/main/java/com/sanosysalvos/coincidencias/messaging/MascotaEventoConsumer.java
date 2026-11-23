package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaEventoDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import com.sanosysalvos.coincidencias.dto.MascotaAlertaDTO;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MascotaEventoConsumer {

    public static final String TOPIC = "mascotas-eventos";

    private static final Logger log =
            LoggerFactory.getLogger(MascotaEventoConsumer.class);
    private static final Set<String> TIPOS_PERMITIDOS =
            Set.of("MASCOTA_REGISTRADA", "MASCOTA_ACTUALIZADA");
    private static final Set<String> ESTADOS_PERMITIDOS =
            Set.of("PERDIDA", "ENCONTRADA");
    private static final int EVENTOS_PROCESADOS_MAXIMOS = 10000;

    private final MascotaAlertaKafkaProducer mascotaAlertaKafkaProducer;

    private final Map<UUID, Boolean> eventosProcesados =
            Collections.synchronizedMap(new LinkedHashMap<>(64, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<UUID, Boolean> eldest) {
                    return size() > EVENTOS_PROCESADOS_MAXIMOS;
                }
            });

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

        boolean nuevoEvento;
        synchronized (eventosProcesados) {
            nuevoEvento = !eventosProcesados.containsKey(evento.getEventId());
            if (nuevoEvento) {
                eventosProcesados.put(evento.getEventId(), Boolean.TRUE);
            }
        }

        if (nuevoEvento) {
            generarAlertaSiCorresponde(evento);
        } else {
            log.info(
                    "[KAFKA] Evento duplicado ignorado eventId={} topic={} partition={} offset={}",
                    evento.getEventId(),
                    record.topic(),
                    record.partition(),
                    record.offset());
        }
    }

    void generarAlertaSiCorresponde(MascotaEventoDTO evento) {
        if (!"MASCOTA_REGISTRADA".equals(evento.getTipoEvento())
                || !"PERDIDA".equals(evento.getEstado())) {
            return;
        }

        MascotaAlertaDTO alerta = MascotaAlertaDTO.builder()
                .alertaId(UUID.randomUUID())
                .eventoOrigenId(evento.getEventId())
                .mascotaId(evento.getMascotaId())
                .usuarioId(evento.getUsuarioId())
                .tipoAlerta("MASCOTA_PERDIDA_REGISTRADA")
                .mensaje("Se registro una mascota perdida con ID "
                        + evento.getMascotaId())
                .timestamp(Instant.now())
                .build();

        mascotaAlertaKafkaProducer.publicar(alerta);
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
