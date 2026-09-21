package com.sanosysalvos.coincidencias.config;

import com.sanosysalvos.coincidencias.controller.CoincidenciaController;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CoincidenciaController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final String ISSUER = "https://sts.windows.net/14cc35b0-80bc-4ffd-b90a-1fc02097a113/";
    private static final String AUDIENCE = "api://d88e02e7-1a2d-4d8c-b8f8-ad917d8adeac";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CoincidenciaService coincidenciaService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/coincidencias")).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSinScopeDevuelve403() throws Exception {
        mockMvc.perform(get("/api/coincidencias").with(jwt().authorities(
                new SimpleGrantedAuthority("SCOPE_otro_scope"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenConScopePermiteAcceso() throws Exception {
        when(coincidenciaService.buscarCoincidencias()).thenReturn(List.of());
        mockMvc.perform(get("/api/coincidencias").with(jwt().authorities(
                new SimpleGrantedAuthority("SCOPE_access_as_user"))))
                .andExpect(status().isOk());
    }

    @Test
    void preflightPermanecePublico() throws Exception {
        mockMvc.perform(options("/api/coincidencias")).andExpect(status().isOk());
    }

    @Test
    void issuerInvalidoEsRechazado() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig().entraJwtValidator(ISSUER, AUDIENCE);
        assertTrue(validator.validate(token(ISSUER + "invalid", AUDIENCE)).hasErrors());
    }

    @Test
    void audienceInvalidoEsRechazado() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig().entraJwtValidator(ISSUER, AUDIENCE);
        assertTrue(validator.validate(token(ISSUER, "api://otra-api")).hasErrors());
    }

    @Test
    void issuerYAudienceValidosSonAceptados() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig().entraJwtValidator(ISSUER, AUDIENCE);
        assertFalse(validator.validate(token(ISSUER, AUDIENCE)).hasErrors());
    }

    private Jwt token(String issuer, String audience) {
        Instant now = Instant.now();
        return Jwt.withTokenValue("test-token").header("alg", "RS256").issuer(issuer)
                .audience(List.of(audience)).issuedAt(now.minusSeconds(30))
                .expiresAt(now.plusSeconds(300)).build();
    }
}
