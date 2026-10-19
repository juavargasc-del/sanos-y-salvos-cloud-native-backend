package com.sanosysalvos.rabbitmqadmin.controller;

import com.sanosysalvos.rabbitmqadmin.dto.BindingRequest;
import com.sanosysalvos.rabbitmqadmin.dto.BindingResponse;
import com.sanosysalvos.rabbitmqadmin.dto.ExchangeRequest;
import com.sanosysalvos.rabbitmqadmin.dto.QueueRequest;
import com.sanosysalvos.rabbitmqadmin.dto.ResourceResponse;
import com.sanosysalvos.rabbitmqadmin.service.RabbitAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/rabbitmq")
@Tag(name = "RabbitMQ Admin", description = "Administración protegida de RabbitMQ")
public class RabbitAdminController {

    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping("/queues")
    @Operation(summary = "Crear una cola durable")
    public ResponseEntity<Void> createQueue(@Valid @RequestBody QueueRequest request) {
        Queue queue = service.createQueue(request);
        return ResponseEntity.created(URI.create("/api/admin/rabbitmq/queues/" + queue.getName()))
                .build();
    }

    @DeleteMapping("/queues/{name}")
    @Operation(summary = "Eliminar una cola")
    public ResponseEntity<Void> deleteQueue(@PathVariable String name) {
        service.deleteQueue(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/queues")
    @Operation(summary = "Listar colas")
    public List<ResourceResponse> queues() {
        return service.queues();
    }

    @PostMapping("/exchanges")
    @Operation(summary = "Crear un exchange direct, topic o fanout")
    public ResponseEntity<Void> createExchange(
            @Valid @RequestBody ExchangeRequest request) {
        Exchange exchange = service.createExchange(request);
        return ResponseEntity.created(
                URI.create("/api/admin/rabbitmq/exchanges/" + exchange.getName())).build();
    }

    @DeleteMapping("/exchanges/{name}")
    @Operation(summary = "Eliminar un exchange")
    public ResponseEntity<Void> deleteExchange(@PathVariable String name) {
        service.deleteExchange(name);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exchanges")
    @Operation(summary = "Listar exchanges")
    public List<ResourceResponse> exchanges() {
        return service.exchanges();
    }

    @PostMapping("/bindings")
    @Operation(summary = "Crear un binding entre exchange y cola")
    public ResponseEntity<BindingResponse> createBinding(
            @Valid @RequestBody BindingRequest request) {
        return ResponseEntity.status(201).body(service.createBinding(request));
    }

    @DeleteMapping("/bindings")
    @Operation(summary = "Eliminar un binding")
    public ResponseEntity<Void> deleteBinding(@Valid @RequestBody BindingRequest request) {
        service.deleteBinding(request);
        return ResponseEntity.noContent().build();
    }
}
