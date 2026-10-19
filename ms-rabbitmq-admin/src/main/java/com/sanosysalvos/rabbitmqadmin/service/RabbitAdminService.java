package com.sanosysalvos.rabbitmqadmin.service;

import com.sanosysalvos.rabbitmqadmin.config.RabbitManagementProperties;
import com.sanosysalvos.rabbitmqadmin.dto.BindingRequest;
import com.sanosysalvos.rabbitmqadmin.dto.BindingResponse;
import com.sanosysalvos.rabbitmqadmin.dto.ExchangeRequest;
import com.sanosysalvos.rabbitmqadmin.dto.QueueRequest;
import com.sanosysalvos.rabbitmqadmin.dto.ResourceResponse;
import com.sanosysalvos.rabbitmqadmin.exception.ProtectedResourceException;
import com.sanosysalvos.rabbitmqadmin.exception.ResourceConflictException;
import com.sanosysalvos.rabbitmqadmin.exception.ResourceNotFoundException;
import com.sanosysalvos.rabbitmqadmin.exception.RabbitManagementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.net.URI;

@Service
public class RabbitAdminService {

    private static final Logger log = LoggerFactory.getLogger(RabbitAdminService.class);
    private static final List<String> PROTECTED_QUEUES = List.of(
            "amq.rabbitmq.log", "mascotas.registradas.queue",
            "mascotas.registradas.dlq");
    private static final List<String> PROTECTED_EXCHANGES = List.of(
            "amq.direct", "amq.fanout", "amq.match", "amq.rabbitmq.log",
            "amq.topic", "mascotas.exchange", "mascotas.dlx");

    private final RabbitAdmin rabbitAdmin;
    private final RestClient managementClient;
    private final String managementBaseUrl;

    public RabbitAdminService(
            RabbitAdmin rabbitAdmin,
            RestClient managementClient,
            RabbitManagementProperties managementProperties) {
        this.rabbitAdmin = rabbitAdmin;
        this.managementClient = managementClient;
        this.managementBaseUrl = managementProperties.baseUrl().replaceAll("/$", "");
    }

    public Queue createQueue(QueueRequest request) {
        Queue queue = new Queue(request.name(), defaultTrue(request.durable()),
                defaultFalse(request.exclusive()), defaultFalse(request.autoDelete()),
                request.arguments() == null ? Map.of() : request.arguments());
        return declare(queue, "cola");
    }

    public void deleteQueue(String name) {
        ensureDeletable(name, PROTECTED_QUEUES, "cola");
        if (!rabbitAdmin.deleteQueue(name)) {
            throw new ResourceNotFoundException("La cola no existe: " + name);
        }
    }

    public Exchange createExchange(ExchangeRequest request) {
        Exchange exchange = switch (request.type().toLowerCase()) {
            case "direct" -> new DirectExchange(request.name(),
                    defaultTrue(request.durable()), defaultFalse(request.autoDelete()),
                    request.arguments());
            case "topic" -> new TopicExchange(request.name(),
                    defaultTrue(request.durable()), defaultFalse(request.autoDelete()),
                    request.arguments());
            case "fanout" -> new FanoutExchange(request.name(),
                    defaultTrue(request.durable()), defaultFalse(request.autoDelete()),
                    request.arguments());
            default -> throw new IllegalArgumentException("Tipo de exchange no soportado");
        };
        return declare(exchange, "exchange");
    }

    public void deleteExchange(String name) {
        ensureDeletable(name, PROTECTED_EXCHANGES, "exchange");
        if (!rabbitAdmin.deleteExchange(name)) {
            throw new ResourceNotFoundException("El exchange no existe: " + name);
        }
    }

    public BindingResponse createBinding(BindingRequest request) {
        Binding binding = binding(request);
        try {
            rabbitAdmin.declareBinding(binding);
            return new BindingResponse(request.exchange(), request.queue(),
                    routingKey(request.routingKey()));
        } catch (Exception exception) {
            throw new ResourceConflictException(
                    "No se pudo crear el binding; revise que existan la cola y el exchange",
                    exception);
        }
    }

    public void deleteBinding(BindingRequest request) {
        try {
            rabbitAdmin.removeBinding(binding(request));
        } catch (Exception exception) {
            throw new ResourceNotFoundException("No se pudo eliminar el binding solicitado");
        }
    }

    public List<ResourceResponse> queues() {
        return resources("/api/queues/%2F", "queue");
    }

    public List<ResourceResponse> exchanges() {
        return resources("/api/exchanges/%2F", "exchange");
    }

    private Binding binding(BindingRequest request) {
        return new Binding(
                request.queue(),
                Binding.DestinationType.QUEUE,
                request.exchange(),
                routingKey(request.routingKey()),
                request.arguments() == null ? Map.of() : request.arguments());
    }

    private String routingKey(String routingKey) {
        return routingKey == null ? "" : routingKey;
    }

    private <T> T declare(T resource, String type) {
        try {
            if (resource instanceof Queue queue) {
                rabbitAdmin.declareQueue(queue);
            } else if (resource instanceof Exchange exchange) {
                rabbitAdmin.declareExchange(exchange);
            }
            return resource;
        } catch (Exception exception) {
            throw new ResourceConflictException(
                    "El " + type + " ya existe con una configuración incompatible",
                    exception);
        }
    }

    private List<ResourceResponse> resources(String path, String type) {
        try {
            Map[] values = managementClient.get()
                    // Use an already encoded absolute URI; template expansion can turn
                    // the vhost escape %2F into %252F.
                    .uri(URI.create(managementBaseUrl + path))
                    .retrieve()
                    .body(Map[].class);
            if (values == null) {
                return List.of();
            }
            return Arrays.stream(values)
                    .map(value -> new ResourceResponse(
                            (String) value.get("name"),
                            type,
                            Boolean.TRUE.equals(value.get("durable"))))
                    .toList();
        } catch (RestClientResponseException exception) {
            log.error("RabbitMQ Management devolvió HTTP {} al consultar {}",
                    exception.getStatusCode().value(), path);
            throw new RabbitManagementException(
                    "RabbitMQ Management rechazó la consulta de " + type, exception);
        } catch (RestClientException exception) {
            log.error("No fue posible consultar RabbitMQ Management en {}: {}",
                    path, exception.getMessage());
            throw new RabbitManagementException(
                    "No fue posible consultar RabbitMQ Management", exception);
        }
    }

    private void ensureDeletable(String name, List<String> protectedNames, String type) {
        if (name.startsWith("amq.") || protectedNames.contains(name)) {
            throw new ProtectedResourceException(
                    "No está permitido eliminar el " + type + " protegido: " + name);
        }
    }

    private boolean defaultTrue(Boolean value) {
        return value == null || value;
    }

    private boolean defaultFalse(Boolean value) {
        return Boolean.TRUE.equals(value);
    }
}
