package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaAlertaDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MascotaAlertaKafkaProducer {

    public static final String TOPIC = "mascotas-alertas";

    private static final Logger log =
            LoggerFactory.getLogger(MascotaAlertaKafkaProducer.class);
    private final KafkaTemplate<String, MascotaAlertaDTO> kafkaTemplate;

    public void publicar(MascotaAlertaDTO alerta) {
        kafkaTemplate.send(TOPIC, alerta.getMascotaId().toString(), alerta)
                .whenComplete((SendResult<String, MascotaAlertaDTO> resultado,
                               Throwable error) -> {
                    if (error != null) {
                        log.error(
                                "[KAFKA] Error publicando alerta topic={} key={} tipoAlerta={} alertaId={} ",
                                TOPIC,
                                alerta.getMascotaId(),
                                alerta.getTipoAlerta(),
                                alerta.getAlertaId(),
                                error);
                        return;
                    }

                    log.info(
                            "[KAFKA] Alerta confirmada topic={} key={} tipoAlerta={} "
                                    + "partition={} offset={} alertaId={}",
                            TOPIC,
                            alerta.getMascotaId(),
                            alerta.getTipoAlerta(),
                            resultado.getRecordMetadata().partition(),
                            resultado.getRecordMetadata().offset(),
                            alerta.getAlertaId());
                });
    }
}
