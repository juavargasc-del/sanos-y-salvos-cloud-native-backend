package com.sanosysalvos.kafkaadmin.messaging;

import com.sanosysalvos.kafkaadmin.dto.MascotaAlertaDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MascotaAlertaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(MascotaAlertaConsumer.class);

    @KafkaListener(
            topics = "mascotas-alertas",
            groupId = "sanosysalvos-alertas")
    public void recibir(ConsumerRecord<String, MascotaAlertaDTO> record) {
        MascotaAlertaDTO alerta = record.value();
        validar(alerta, record.key());
        log.info(
                "[KAFKA] Alerta recibida alertaId={} mascotaId={} tipoAlerta={} "
                        + "topic={} partition={} offset={} key={}",
                alerta.getAlertaId(),
                alerta.getMascotaId(),
                alerta.getTipoAlerta(),
                record.topic(),
                record.partition(),
                record.offset(),
                record.key());
    }

    void validar(MascotaAlertaDTO alerta, String key) {
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta Kafka nula");
        }
        if (alerta.getAlertaId() == null) {
            throw new IllegalArgumentException("Alerta sin alertaId");
        }
        if (alerta.getEventoOrigenId() == null) {
            throw new IllegalArgumentException("Alerta sin eventoOrigenId");
        }
        if (alerta.getMascotaId() == null) {
            throw new IllegalArgumentException("Alerta sin mascotaId");
        }
        if (alerta.getTipoAlerta() == null || alerta.getTipoAlerta().isBlank()) {
            throw new IllegalArgumentException("Alerta sin tipoAlerta");
        }
        if (alerta.getMensaje() == null || alerta.getMensaje().isBlank()) {
            throw new IllegalArgumentException("Alerta sin mensaje");
        }
        if (alerta.getTimestamp() == null) {
            throw new IllegalArgumentException("Alerta sin timestamp");
        }
        if (key == null || !key.equals(alerta.getMascotaId().toString())) {
            throw new IllegalArgumentException(
                    "La clave no coincide con mascotaId");
        }
    }
}
