package com.sanosysalvos.coincidencias.messaging;

import com.sanosysalvos.coincidencias.dto.MascotaAlertaDTO;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotaAlertaKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, MascotaAlertaDTO> kafkaTemplate;

    @Test
    void debeEnviarAlertaAlTopicConMascotaIdComoKey() {
        MascotaAlertaKafkaProducer producer =
                new MascotaAlertaKafkaProducer(kafkaTemplate);
        MascotaAlertaDTO alerta = alertaValida();

        @SuppressWarnings("unchecked")
        SendResult<String, MascotaAlertaDTO> sendResult =
                org.mockito.Mockito.mock(SendResult.class);
        RecordMetadata metadata =
                org.mockito.Mockito.mock(RecordMetadata.class);
        when(metadata.partition()).thenReturn(2);
        when(metadata.offset()).thenReturn(7L);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(kafkaTemplate.send("mascotas-alertas", "42", alerta))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        producer.publicar(alerta);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MascotaAlertaDTO> alertaCaptor =
                ArgumentCaptor.forClass(MascotaAlertaDTO.class);
        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                alertaCaptor.capture());
        assertEquals("mascotas-alertas", topicCaptor.getValue());
        assertEquals("42", keyCaptor.getValue());
        assertEquals(alerta, alertaCaptor.getValue());
    }

    @Test
    void debeRegistrarElErrorAsincronoSinLanzarExcepcion() {
        MascotaAlertaKafkaProducer producer =
                new MascotaAlertaKafkaProducer(kafkaTemplate);
        MascotaAlertaDTO alerta = alertaValida();

        CompletableFuture<SendResult<String, MascotaAlertaDTO>> fallido =
                new CompletableFuture<>();
        fallido.completeExceptionally(new IllegalStateException("Kafka no disponible"));
        when(kafkaTemplate.send("mascotas-alertas", "42", alerta))
                .thenReturn(fallido);

        producer.publicar(alerta);

        verify(kafkaTemplate).send("mascotas-alertas", "42", alerta);
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
