package com.sanosysalvos.kafkaadmin.service;

import com.sanosysalvos.kafkaadmin.dto.ConsumerGroupResponse;
import com.sanosysalvos.kafkaadmin.dto.ConsumerOffsetResponse;
import com.sanosysalvos.kafkaadmin.dto.CreateTopicRequest;
import com.sanosysalvos.kafkaadmin.dto.TopicConfigUpdateRequest;
import com.sanosysalvos.kafkaadmin.dto.TopicResponse;
import com.sanosysalvos.kafkaadmin.exception.InvalidRequestException;
import com.sanosysalvos.kafkaadmin.exception.KafkaAdminException;
import com.sanosysalvos.kafkaadmin.exception.ResourceConflictException;
import com.sanosysalvos.kafkaadmin.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class KafkaAdminService {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final Set<String> ALLOWED_TOPIC_CONFIGS =
            Set.of("retention.ms", "cleanup.policy");

    private final AdminClient adminClient;

    public List<String> listTopics() {
        try {
            Set<String> names = adminClient.listTopics().names()
                    .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            return names.stream().sorted().toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException("Interrupcion al listar topicos", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException("No fue posible listar topicos", e);
        }
    }

    public TopicResponse describeTopic(String name) {
        try {
            if (!listTopics().contains(name)) {
                throw new ResourceNotFoundException("Topico no encontrado: " + name);
            }
            DescribeTopicsResult topicResult =
                    adminClient.describeTopics(List.of(name));
            TopicDescription description =
                    topicResult.topicNameValues().get(name)
                            .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            ConfigResource resource =
                    new ConfigResource(ConfigResource.Type.TOPIC, name);
            Config config = adminClient.describeConfigs(List.of(resource))
                    .all().get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                    .get(resource);

            Map<String, String> relevantConfigs = new TreeMap<>();
            if (config != null) {
                for (ConfigEntry entry : config.entries()) {
                    if (ALLOWED_TOPIC_CONFIGS.contains(entry.name())) {
                        relevantConfigs.put(entry.name(), entry.value());
                    }
                }
            }

            short replicationFactor = 0;
            if (!description.partitions().isEmpty()) {
                replicationFactor = (short) description.partitions().get(0)
                        .replicas().size();
            }

            return TopicResponse.builder()
                    .name(name)
                    .partitions(description.partitions().size())
                    .replicationFactor(replicationFactor)
                    .configurations(relevantConfigs)
                    .build();
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException("Interrupcion al describir topico", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException("No fue posible describir el topico", e);
        }
    }

    public TopicResponse createTopic(CreateTopicRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("El nombre del topico es obligatorio");
        }
        if (request.getPartitions() < 1) {
            throw new InvalidRequestException("partitions debe ser al menos 1");
        }
        if (request.getReplicationFactor() < 1) {
            throw new InvalidRequestException(
                    "replicationFactor debe ser al menos 1");
        }
        if (request.getRetentionMs() != null
                && request.getRetentionMs() < -1) {
            throw new InvalidRequestException(
                    "retentionMs debe ser positivo o -1");
        }
        if (listTopics().contains(request.getName())) {
            throw new ResourceConflictException(
                    "El topico ya existe: " + request.getName());
        }

        NewTopic newTopic = new NewTopic(
                request.getName(),
                request.getPartitions(),
                request.getReplicationFactor());
        if (request.getRetentionMs() != null) {
            newTopic.configs(Map.of("retention.ms",
                    request.getRetentionMs().toString()));
        }

        try {
            adminClient.createTopics(List.of(newTopic)).all()
                    .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            return describeTopic(request.getName());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException("Interrupcion al crear topico", e);
        } catch (ExecutionException | TimeoutException e) {
            Throwable cause = e.getCause();
            if (cause instanceof org.apache.kafka.common.errors.TopicExistsException) {
                throw new ResourceConflictException(
                        "El topico ya existe: " + request.getName());
            }
            throw new KafkaAdminException("No fue posible crear el topico", e);
        }
    }

    public TopicResponse alterTopicConfig(String name,
                                          TopicConfigUpdateRequest request) {
        if (!listTopics().contains(name)) {
            throw new ResourceNotFoundException("Topico no encontrado: " + name);
        }
        if (request.getRetentionMs() == null
                && (request.getCleanupPolicy() == null
                    || request.getCleanupPolicy().isBlank())) {
            throw new InvalidRequestException(
                    "Debe especificar retentionMs o cleanupPolicy");
        }
        if (request.getRetentionMs() != null
                && request.getRetentionMs() < -1) {
            throw new InvalidRequestException(
                    "retentionMs debe ser positivo o -1");
        }

        ConfigResource resource =
                new ConfigResource(ConfigResource.Type.TOPIC, name);
        Collection<AlterConfigOp> operations = new ArrayList<>();
        if (request.getRetentionMs() != null) {
            operations.add(new AlterConfigOp(
                    new ConfigEntry("retention.ms",
                            request.getRetentionMs().toString()),
                    AlterConfigOp.OpType.SET));
        }
        if (request.getCleanupPolicy() != null
                && !request.getCleanupPolicy().isBlank()) {
            if (!Set.of("delete", "compact", "compact,delete", "delete,compact")
                    .contains(request.getCleanupPolicy())) {
                throw new InvalidRequestException(
                        "cleanupPolicy no permitido");
            }
            operations.add(new AlterConfigOp(
                    new ConfigEntry("cleanup.policy",
                            request.getCleanupPolicy()),
                    AlterConfigOp.OpType.SET));
        }

        try {
            adminClient.incrementalAlterConfigs(
                    Map.of(resource, operations)).all()
                    .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            return describeTopic(name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException("Interrupcion al modificar topico", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException(
                    "No fue posible modificar el topico", e);
        }
    }

    public List<String> listConsumerGroups() {
        try {
            Collection<ConsumerGroupListing> listings =
                    adminClient.listConsumerGroups().all()
                            .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            return listings.stream()
                    .map(ConsumerGroupListing::groupId)
                    .sorted()
                    .toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException(
                    "Interrupcion al listar grupos", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException(
                    "No fue posible listar grupos", e);
        }
    }

    public ConsumerGroupResponse describeConsumerGroup(String groupId) {
        try {
            ConsumerGroupDescription description =
                    adminClient.describeConsumerGroups(List.of(groupId))
                            .describedGroups()
                            .get(groupId)
                            .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            Map<String, List<String>> assignments = new HashMap<>();
            List<String> memberIds = new ArrayList<>();
            description.members().forEach(member -> {
                memberIds.add(member.consumerId());
                member.assignment().topicPartitions().forEach(partition ->
                        assignments.computeIfAbsent(partition.topic(),
                                        ignored -> new ArrayList<>())
                                .add(String.valueOf(partition.partition())));
            });

            return ConsumerGroupResponse.builder()
                    .groupId(groupId)
                    .state(description.state().toString())
                    .protocolType("")
                    .members(memberIds)
                    .assignedPartitions(assignments)
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException(
                    "Interrupcion al describir grupo", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException(
                    "No fue posible describir el grupo", e);
        }
    }

    public List<ConsumerOffsetResponse> consumerOffsets(String groupId) {
        try {
            Map<TopicPartition, OffsetAndMetadata> committed =
                    adminClient.listConsumerGroupOffsets(groupId)
                            .partitionsToOffsetAndMetadata()
                            .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            if (committed.isEmpty()) {
                return Collections.emptyList();
            }

            Map<TopicPartition, org.apache.kafka.clients.admin.OffsetSpec>
                    requests = new HashMap<>();
            committed.keySet().forEach(partition ->
                    requests.put(partition,
                            org.apache.kafka.clients.admin.OffsetSpec.latest()));

            Map<TopicPartition,
                    org.apache.kafka.clients.admin.ListOffsetsResult
                            .ListOffsetsResultInfo> endings =
                    adminClient.listOffsets(requests).all()
                            .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            List<ConsumerOffsetResponse> responses = new ArrayList<>();
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry :
                    committed.entrySet()) {
                TopicPartition partition = entry.getKey();
                OffsetAndMetadata metadata = entry.getValue();
                Long committedOffset =
                        metadata == null ? null : metadata.offset();
                org.apache.kafka.clients.admin.ListOffsetsResult
                        .ListOffsetsResultInfo end =
                        endings.get(partition);
                Long endOffset = end == null ? null : end.offset();
                Long lag = committedOffset == null || endOffset == null
                        ? null
                        : Math.max(0L, endOffset - committedOffset);
                responses.add(ConsumerOffsetResponse.builder()
                        .topic(partition.topic())
                        .partition(partition.partition())
                        .committedOffset(committedOffset)
                        .endOffset(endOffset)
                        .lag(lag)
                        .build());
            }
            responses.sort(java.util.Comparator
                    .comparing(ConsumerOffsetResponse::getTopic)
                    .thenComparing(ConsumerOffsetResponse::getPartition));
            return responses;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaAdminException(
                    "Interrupcion al consultar offsets", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new KafkaAdminException(
                    "No fue posible consultar offsets", e);
        }
    }
}
