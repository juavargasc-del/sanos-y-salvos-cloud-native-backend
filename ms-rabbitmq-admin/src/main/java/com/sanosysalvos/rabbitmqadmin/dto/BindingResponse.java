package com.sanosysalvos.rabbitmqadmin.dto;

public record BindingResponse(String exchange, String queue, String routingKey) {
}
