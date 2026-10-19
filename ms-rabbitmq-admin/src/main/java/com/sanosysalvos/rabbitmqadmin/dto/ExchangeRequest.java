package com.sanosysalvos.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Map;

public record ExchangeRequest(
        @NotBlank(message = "El nombre del exchange es obligatorio")
        String name,
        @NotBlank(message = "El tipo de exchange es obligatorio")
        @Pattern(regexp = "direct|topic|fanout",
                message = "El tipo debe ser direct, topic o fanout")
        String type,
        Boolean durable,
        Boolean autoDelete,
        Map<String, Object> arguments) {
}
