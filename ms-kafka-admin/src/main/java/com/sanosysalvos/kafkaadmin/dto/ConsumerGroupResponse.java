package com.sanosysalvos.kafkaadmin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ConsumerGroupResponse {
    private String groupId;
    private String state;
    private String protocolType;
    private List<String> members;
    private Map<String, List<String>> assignedPartitions;
}
