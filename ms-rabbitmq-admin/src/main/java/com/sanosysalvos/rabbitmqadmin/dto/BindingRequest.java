package com.sanosysalvos.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record BindingRequest(
        @NotBlank(message = "El exchange es obligatorio")
        String exchange,
        @NotBlank(message = "La cola es obligatoria")
        String queue,
        String routingKey,
        Map<String, Object> arguments) {
}
