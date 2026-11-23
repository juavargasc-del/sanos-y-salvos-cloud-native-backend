package com.sanosysalvos.kafkaadmin.service;

import com.sanosysalvos.kafkaadmin.dto.CreateTopicRequest;
import com.sanosysalvos.kafkaadmin.exception.InvalidRequestException;
import com.sanosysalvos.kafkaadmin.exception.ResourceConflictException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class KafkaAdminServiceTest {

    @Mock
    private AdminClient adminClient;

    @Test
    void rechazaCreacionSinNombre() throws Exception {
        KafkaAdminService service = new KafkaAdminService(adminClient);
        CreateTopicRequest request = new CreateTopicRequest();
        request.setPartitions(3);
        request.setReplicationFactor((short) 1);

        assertThrows(InvalidRequestException.class,
                () -> service.createTopic(request));
    }

    @Test
    void rechazaParticionesInvalidas() throws Exception {
        KafkaAdminService service = new KafkaAdminService(adminClient);
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("mascotas-prueba");
        request.setPartitions(0);
        request.setReplicationFactor((short) 1);

        assertThrows(InvalidRequestException.class,
                () -> service.createTopic(request));
    }

    @Test
    void detectaTopicoDuplicado() throws Exception {
        ListTopicsResult result = mock(ListTopicsResult.class);
        KafkaFuture<Set<String>> future = mock(KafkaFuture.class);
        when(adminClient.listTopics()).thenReturn(result);
        when(result.names()).thenReturn(future);
        when(future.get(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any())).thenReturn(
                Set.of("mascotas-alertas"));

        KafkaAdminService service = new KafkaAdminService(adminClient);
        CreateTopicRequest request = new CreateTopicRequest();
        request.setName("mascotas-alertas");
        request.setPartitions(3);
        request.setReplicationFactor((short) 1);

        assertThrows(ResourceConflictException.class,
                () -> service.createTopic(request));
    }

    @Test
    void listaTopicosOrdenados() throws Exception {
        ListTopicsResult result = mock(ListTopicsResult.class);
        KafkaFuture<Set<String>> future = mock(KafkaFuture.class);
        when(adminClient.listTopics()).thenReturn(result);
        when(result.names()).thenReturn(future);
        when(future.get(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any())).thenReturn(
                Set.of("mascotas-eventos", "mascotas-alertas"));

        KafkaAdminService service = new KafkaAdminService(adminClient);

        assertEquals(
                java.util.List.of("mascotas-alertas", "mascotas-eventos"),
                service.listTopics());
    }

    @Test
    void offsetsSinConfirmacionRetornaListaSinLagCalculado() throws Exception {
        ListConsumerGroupOffsetsResult result =
                mock(ListConsumerGroupOffsetsResult.class);
        KafkaFuture<Map<org.apache.kafka.common.TopicPartition,
                org.apache.kafka.clients.consumer.OffsetAndMetadata>> future =
                mock(KafkaFuture.class);
        when(adminClient.listConsumerGroupOffsets("grupo-nuevo"))
                .thenReturn(result);
        when(result.partitionsToOffsetAndMetadata()).thenReturn(future);
        when(future.get(org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any())).thenReturn(Map.of());

        KafkaAdminService service = new KafkaAdminService(adminClient);

        assertEquals(
                java.util.Collections.emptyList(),
                service.consumerOffsets("grupo-nuevo"));
    }
}
