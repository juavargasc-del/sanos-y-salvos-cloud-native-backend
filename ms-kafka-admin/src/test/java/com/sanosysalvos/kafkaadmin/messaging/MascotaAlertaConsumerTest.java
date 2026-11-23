package com.sanosysalvos.kafkaadmin.messaging;

import com.sanosysalvos.kafkaadmin.dto.MascotaAlertaDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MascotaAlertaConsumerTest {

    private final MascotaAlertaConsumer consumer =
            new MascotaAlertaConsumer();

    @Test
    void recibeAlertaValida() {
        MascotaAlertaDTO alerta = alertaValida();
        ConsumerRecord<String, MascotaAlertaDTO> record =
                new ConsumerRecord<>("mascotas-alertas", 1, 9L, "42", alerta);
        assertDoesNotThrow(() -> consumer.recibir(record));
    }

    @Test
    void rechazaAlertaSinAlertaId() {
        MascotaAlertaDTO alerta = alertaValida();
        alerta.setAlertaId(null);
        ConsumerRecord<String, MascotaAlertaDTO> record =
                new ConsumerRecord<>("mascotas-alertas", 0, 1L, "42", alerta);
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class,
                        () -> consumer.recibir(record));
        assertTrue(error.getMessage().contains("alertaId"));
    }

    @Test
    void rechazaAlertaSinMascotaId() {
        MascotaAlertaDTO alerta = alertaValida();
        alerta.setMascotaId(null);
        ConsumerRecord<String, MascotaAlertaDTO> record =
                new ConsumerRecord<>("mascotas-alertas", 0, 2L, "42", alerta);
        assertThrows(IllegalArgumentException.class,
                () -> consumer.recibir(record));
    }

    @Test
    void rechazaClaveIncorrecta() {
        MascotaAlertaDTO alerta = alertaValida();
        ConsumerRecord<String, MascotaAlertaDTO> record =
                new ConsumerRecord<>("mascotas-alertas", 0, 3L, "99", alerta);
        IllegalArgumentException error =
                assertThrows(IllegalArgumentException.class,
                        () -> consumer.recibir(record));
        assertTrue(error.getMessage().contains("clave"));
    }

    private MascotaAlertaDTO alertaValida() {
        return MascotaAlertaDTO.builder()
                .alertaId(UUID.randomUUID())
                .eventoOrigenId(UUID.randomUUID())
                .mascotaId(42L)
                .usuarioId(10L)
                .tipoAlerta("MASCOTA_PERDIDA_REGISTRADA")
                .mensaje("Se registro una mascota perdida con ID 42")
                .timestamp(Instant.now())
                .build();
    }
}
