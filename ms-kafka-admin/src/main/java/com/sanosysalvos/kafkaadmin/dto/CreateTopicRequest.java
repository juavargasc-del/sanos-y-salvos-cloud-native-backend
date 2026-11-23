package com.sanosysalvos.kafkaadmin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTopicRequest {
    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._-]+")
    private String name;

    @Min(1)
    private int partitions;

    @Min(1)
    private short replicationFactor;

    private Long retentionMs;
}
