package com.sanosysalvos.kafkaadmin.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicConfigUpdateRequest {
    private Long retentionMs;

    @Pattern(regexp = "delete|compact|compact,delete|delete,compact")
    private String cleanupPolicy;
}
