package com.sanosysalvos.kafkaadmin.controller;

import com.sanosysalvos.kafkaadmin.dto.ConsumerGroupResponse;
import com.sanosysalvos.kafkaadmin.dto.ConsumerOffsetResponse;
import com.sanosysalvos.kafkaadmin.dto.CreateTopicRequest;
import com.sanosysalvos.kafkaadmin.dto.TopicConfigUpdateRequest;
import com.sanosysalvos.kafkaadmin.dto.TopicResponse;
import com.sanosysalvos.kafkaadmin.service.KafkaAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kafka/admin")
@RequiredArgsConstructor
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    @GetMapping("/topics")
    public List<String> listTopics() {
        return kafkaAdminService.listTopics();
    }

    @GetMapping("/topics/{name}")
    public TopicResponse describeTopic(@PathVariable String name) {
        return kafkaAdminService.describeTopic(name);
    }

    @PostMapping("/topics")
    public ResponseEntity<TopicResponse> createTopic(
            @Valid @RequestBody CreateTopicRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(kafkaAdminService.createTopic(request));
    }

    @PatchMapping("/topics/{name}/config")
    public TopicResponse alterTopicConfig(
            @PathVariable String name,
            @Valid @RequestBody TopicConfigUpdateRequest request) {
        return kafkaAdminService.alterTopicConfig(name, request);
    }

    @GetMapping("/consumer-groups")
    public List<String> listConsumerGroups() {
        return kafkaAdminService.listConsumerGroups();
    }

    @GetMapping("/consumer-groups/{groupId}")
    public ConsumerGroupResponse describeConsumerGroup(
            @PathVariable String groupId) {
        return kafkaAdminService.describeConsumerGroup(groupId);
    }

    @GetMapping("/consumer-groups/{groupId}/offsets")
    public List<ConsumerOffsetResponse> consumerOffsets(
            @PathVariable String groupId) {
        return kafkaAdminService.consumerOffsets(groupId);
    }
}
