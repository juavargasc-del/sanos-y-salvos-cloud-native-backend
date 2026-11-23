package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaEventoDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MascotaEventoConsumerTest {

    private final MascotaEventoConsumer consumer = new MascotaEventoConsumer();

    @Test
    void debeAceptarEventoRegistradaValido() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "PERDIDA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 12L, "42", evento);

        assertDoesNotThrow(() -> consumer.recibir(record));
    }

    @Test
    void debeAceptarEventoActualizadaValido() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_ACTUALIZADA",
                "ENCONTRADA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 1, 7L, "42", evento);

        assertDoesNotThrow(() -> consumer.recibir(record));
    }

    @Test
    void debeRechazarEventoSinEventId() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "PERDIDA");
        evento.setEventId(null);
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 1L, "42", evento);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("eventId"));
    }

    @Test
    void debeRechazarEventoSinMascotaId() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_ACTUALIZADA",
                "PERDIDA");
        evento.setMascotaId(null);
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 2L, "42", evento);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("mascotaId"));
    }

    @Test
    void debeRechazarTipoEventoDesconocido() {
        MascotaEventoDTO evento = eventoValido(
                "OTRO_EVENTO",
                "PERDIDA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 3L, "42", evento);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("tipoEvento"));
    }

    @Test
    void debeRechazarEstadoDesconocido() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "DESCONOCIDO");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 4L, "42", evento);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("estado"));
    }

    @Test
    void debeRechazarClaveDistintaDeMascotaId() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "PERDIDA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 5L, "99", evento);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("clave"));
    }

    @Test
    void debeRechazarEventoNuloComoErrorDeDeserializacion() {
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 6L, "42", null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.recibir(record));

        assertTrue(error.getMessage().contains("nulo"));
    }

    private MascotaEventoDTO eventoValido(String tipoEvento, String estado) {
        return MascotaEventoDTO.builder()
                .eventId(UUID.randomUUID())
                .tipoEvento(tipoEvento)
                .mascotaId(42L)
                .nombre("Luna")
                .estado(estado)
                .usuarioId(10L)
                .timestamp(Instant.now())
                .build();
    }
}
