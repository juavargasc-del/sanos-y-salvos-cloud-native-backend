package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaEventoDTO;
import com.sanosysalvos.coincidencias.dto.MascotaAlertaDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class MascotaEventoConsumerTest {

    @Mock
    private MascotaAlertaKafkaProducer mascotaAlertaKafkaProducer;

    private MascotaEventoConsumer consumer;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        consumer = new MascotaEventoConsumer(mascotaAlertaKafkaProducer);
    }

    @Test
    void debeAceptarEventoRegistradaValido() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "PERDIDA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 0, 12L, "42", evento);

        assertDoesNotThrow(() -> consumer.recibir(record));
        ArgumentCaptor<MascotaAlertaDTO> captor =
                ArgumentCaptor.forClass(MascotaAlertaDTO.class);
        verify(mascotaAlertaKafkaProducer).publicar(captor.capture());
        MascotaAlertaDTO alerta = captor.getValue();
        assertEquals("MASCOTA_PERDIDA_REGISTRADA", alerta.getTipoAlerta());
        assertEquals(evento.getEventId(), alerta.getEventoOrigenId());
        assertEquals(42L, alerta.getMascotaId());
        assertEquals(10L, alerta.getUsuarioId());
        assertTrue(alerta.getMensaje().contains("42"));
    }

    @Test
    void debeAceptarEventoActualizadaValido() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_ACTUALIZADA",
                "ENCONTRADA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 1, 7L, "42", evento);

        assertDoesNotThrow(() -> consumer.recibir(record));
        verify(mascotaAlertaKafkaProducer, never()).publicar(any());
    }

    @Test
    void noDebeGenerarAlertaParaRegistradaEncontrada() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "ENCONTRADA");
        ConsumerRecord<String, MascotaEventoDTO> record =
                new ConsumerRecord<>("mascotas-eventos", 2, 8L, "42", evento);

        assertDoesNotThrow(() -> consumer.recibir(record));
        verify(mascotaAlertaKafkaProducer, never()).publicar(any());
    }

    @Test
    void debeIgnorarElMismoEventIdDentroDeLaEjecucion() {
        MascotaEventoDTO evento = eventoValido(
                "MASCOTA_REGISTRADA",
                "PERDIDA");
        ConsumerRecord<String, MascotaEventoDTO> primero =
                new ConsumerRecord<>("mascotas-eventos", 0, 12L, "42", evento);
        ConsumerRecord<String, MascotaEventoDTO> segundo =
                new ConsumerRecord<>("mascotas-eventos", 0, 13L, "42", evento);

        consumer.recibir(primero);
        consumer.recibir(segundo);

        verify(mascotaAlertaKafkaProducer, times(1)).publicar(any());
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
