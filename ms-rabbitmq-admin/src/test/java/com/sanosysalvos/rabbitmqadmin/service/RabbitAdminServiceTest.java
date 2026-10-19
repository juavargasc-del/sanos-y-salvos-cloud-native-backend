package com.sanosysalvos.rabbitmqadmin.service;

import com.sanosysalvos.rabbitmqadmin.dto.BindingRequest;
import com.sanosysalvos.rabbitmqadmin.dto.ExchangeRequest;
import com.sanosysalvos.rabbitmqadmin.dto.QueueRequest;
import com.sanosysalvos.rabbitmqadmin.dto.ResourceResponse;
import com.sanosysalvos.rabbitmqadmin.config.RabbitManagementProperties;
import com.sanosysalvos.rabbitmqadmin.exception.ProtectedResourceException;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class RabbitAdminServiceTest {

    private final RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
    private final RestClient managementClient = mock(RestClient.class);
    private final RabbitAdminService service =
            new RabbitAdminService(rabbitAdmin, managementClient, properties());

    @Test
    void createQueue_debeDeclararColaDurable() {
        QueueRequest request = new QueueRequest(
                "cola-prueba", true, false, false, Map.of());
        doReturn("cola-prueba").when(rabbitAdmin).declareQueue(any(Queue.class));

        Queue result = service.createQueue(request);

        assertEquals("cola-prueba", result.getName());
        assertEquals(true, result.isDurable());
        verify(rabbitAdmin).declareQueue(any(Queue.class));
    }

    @Test
    void createExchange_debeUsarTipoTopic() {
        ExchangeRequest request = new ExchangeRequest(
                "exchange-prueba", "topic", true, false, Map.of());

        Exchange result = service.createExchange(request);

        assertEquals("exchange-prueba", result.getName());
        assertEquals("topic", result.getType());
        verify(rabbitAdmin).declareExchange(any(Exchange.class));
    }

    @Test
    void createBinding_debeDeclararBinding() {
        BindingRequest request = new BindingRequest(
                "exchange-prueba", "cola-prueba", "clave", Map.of());

        service.createBinding(request);

        verify(rabbitAdmin).declareBinding(any(Binding.class));
    }

    @Test
    void deleteQueue_noDebeEliminarColaDeMascotas() {
        assertThrows(ProtectedResourceException.class,
                () -> service.deleteQueue("mascotas.registradas.queue"));
    }

    @Test
    void deleteExchange_noDebeEliminarExchangeDelSistema() {
        assertThrows(ProtectedResourceException.class,
                () -> service.deleteExchange("amq.direct"));
    }

    @Test
    void queues_debeConservarLaCodificacionDelVhostRaiz() {
        RestClient.Builder clientBuilder = RestClient.builder()
                .baseUrl("http://rabbitmq:15672")
                .defaultHeaders(headers -> headers.setBasicAuth("guest", "guest"));
        MockRestServiceServer server = MockRestServiceServer.bindTo(clientBuilder).build();
        RestClient client = clientBuilder.build();
        RabbitAdminService serviceWithHttpClient =
                new RabbitAdminService(rabbitAdmin, client, properties());

        server.expect(requestTo("http://rabbitmq:15672/api/queues/%2F"))
                .andRespond(withSuccess(
                        "[{\"name\":\"cola-prueba\",\"durable\":true}]",
                        MediaType.APPLICATION_JSON));

        List<ResourceResponse> result = serviceWithHttpClient.queues();

        assertEquals("cola-prueba", result.get(0).name());
        server.verify();
    }

    @Test
    void exchanges_debeConsultarElVhostRaizSinDobleCodificarlo() {
        RestClient.Builder clientBuilder = RestClient.builder()
                .baseUrl("http://rabbitmq:15672");
        MockRestServiceServer server = MockRestServiceServer.bindTo(clientBuilder).build();
        RestClient client = clientBuilder.build();
        RabbitAdminService serviceWithHttpClient =
                new RabbitAdminService(rabbitAdmin, client, properties());

        server.expect(requestTo("http://rabbitmq:15672/api/exchanges/%2F"))
                .andRespond(withSuccess(
                        "[{\"name\":\"exchange-prueba\",\"durable\":true}]",
                        MediaType.APPLICATION_JSON));

        List<ResourceResponse> result = serviceWithHttpClient.exchanges();

        assertEquals("exchange-prueba", result.get(0).name());
        server.verify();
    }

    private RabbitManagementProperties properties() {
        return new RabbitManagementProperties(
                "http://rabbitmq:15672", "guest", "guest");
    }
}
