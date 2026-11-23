package com.sanosysalvos.rabbitmqadmin.config;

import com.sanosysalvos.rabbitmqadmin.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local-test")
@TestPropertySource(properties = {
        "rabbitmq.admin.local.username=test-admin",
        "rabbitmq.admin.local.password=test-password",
        "rabbitmq.admin.authority=ROLE_RABBITMQ_ADMIN"
})
class LocalTestSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitAdminService rabbitAdminService;

    @Test
    void endpointAdministrativo_sinCredenciales_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/admin/rabbitmq/queues"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void endpointAdministrativo_conCredencialesValidas_debeResponder200() throws Exception {
        when(rabbitAdminService.queues()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/rabbitmq/queues")
                        .with(httpBasic("test-admin", "test-password"))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void endpointAdministrativo_conCredencialesInvalidas_debeResponder401()
            throws Exception {
        mockMvc.perform(get("/api/admin/rabbitmq/queues")
                        .with(httpBasic("test-admin", "wrong-password")))
                .andExpect(status().isUnauthorized());
    }
}
