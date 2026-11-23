package com.sanosysalvos.kafkaadmin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class TopicResponse {
    private String name;
    private Integer partitions;
    private Short replicationFactor;
    private Map<String, String> configurations;
}
