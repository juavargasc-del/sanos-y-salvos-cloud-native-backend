package com.sanosysalvos.kafkaadmin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsumerOffsetResponse {
    private String topic;
    private Integer partition;
    private Long committedOffset;
    private Long endOffset;
    private Long lag;
}
