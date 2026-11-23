package com.sanosysalvos.rabbitmqadmin.controller;

import com.sanosysalvos.rabbitmqadmin.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RabbitAdminControllerTest {

    private final RabbitAdminService service = mock(RabbitAdminService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new RabbitAdminController(service))
            .build();

    @Test
    void createQueue_debeRechazarNombreVacio() throws Exception {
        mockMvc.perform(post("/api/admin/rabbitmq/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","durable":true}
                                """))
                .andExpect(status().isBadRequest());
    }
}
