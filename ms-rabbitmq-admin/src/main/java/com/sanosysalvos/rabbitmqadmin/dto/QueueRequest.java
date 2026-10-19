package com.sanosysalvos.rabbitmqadmin.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public record QueueRequest(
        @NotBlank(message = "El nombre de la cola es obligatorio")
        String name,
        Boolean durable,
        Boolean exclusive,
        Boolean autoDelete,
        Map<String, Object> arguments) {
}
