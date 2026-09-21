package com.sanosysalvos.usuarios.config;

import com.sanosysalvos.usuarios.controller.UserController;
import com.sanosysalvos.usuarios.dto.CurrentUserResponseDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    private static final String ISSUER =
            "https://sts.windows.net/14cc35b0-80bc-4ffd-b90a-1fc02097a113/";
    private static final String AUDIENCE =
            "api://d88e02e7-1a2d-4d8c-b8f8-ad917d8adeac";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void registroPermanecePublico() throws Exception {
        User user = User.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@example.com")
                .password("secreta")
                .rol("USUARIO")
                .build();
        when(userService.guardarUsuario(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/usuarios")
                        .contentType("application/json")
                        .content("""
                                {"nombre":"Ana","email":"ana@example.com","password":"secreta"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void loginPermanecePublico() throws Exception {
        when(userService.login("ana@example.com", "incorrecta"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/usuarios/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"ana@example.com","password":"incorrecta"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endpointProtegidoSinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenSinScopeRequeridoDevuelve403() throws Exception {
        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("SCOPE_otro_scope"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void tokenConScopeRequeridoPuedeAcceder() throws Exception {
        when(userService.listarUsuarios()).thenReturn(List.of());

        mockMvc.perform(get("/api/usuarios")
                        .with(jwt().authorities(
                                new SimpleGrantedAuthority("SCOPE_access_as_user"))))
                .andExpect(status().isOk());
    }

    @Test
    void meDevuelveUsuarioVinculadoSinDatosSensibles() throws Exception {
        when(userService.currentUser("tenant", "object"))
                .thenReturn(CurrentUserResponseDTO.builder()
                        .linked(true)
                        .userId(1L)
                        .nombre("Ana")
                        .email("ana@example.com")
                        .rol("USUARIO")
                        .build());

        mockMvc.perform(get("/api/usuarios/me").with(jwt()
                        .jwt(token -> token.claim("tid", "tenant").claim("oid", "object"))
                        .authorities(new SimpleGrantedAuthority("SCOPE_access_as_user"))))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.userId").value(1))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.linked").value(true))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    void meIndicaCuentaNoVinculadaSinBuscarPorCorreo() throws Exception {
        when(userService.currentUser("tenant", "object"))
                .thenReturn(CurrentUserResponseDTO.builder()
                        .linked(false)
                        .message("La cuenta Microsoft debe vincularse con un usuario local")
                        .build());

        mockMvc.perform(get("/api/usuarios/me").with(jwt()
                        .jwt(token -> token.claim("tid", "tenant").claim("oid", "object"))
                        .authorities(new SimpleGrantedAuthority("SCOPE_access_as_user"))))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.linked").value(false));
    }

    @Test
    void linkMicrosoftUsaLaIdentidadDelJwt() throws Exception {
        when(userService.linkMicrosoftIdentity("tenant", "object", "ana@example.com", "secreta"))
                .thenReturn(CurrentUserResponseDTO.builder()
                        .linked(true).userId(1L).nombre("Ana")
                        .email("ana@example.com").rol("USUARIO").build());

        mockMvc.perform(post("/api/usuarios/link-microsoft")
                        .with(jwt().jwt(token -> token.claim("tid", "tenant").claim("oid", "object"))
                                .authorities(new SimpleGrantedAuthority("SCOPE_access_as_user")))
                        .contentType("application/json")
                        .content("""
                                {"email":"ana@example.com","password":"secreta","userId":99,"tid":"fake","oid":"fake","rol":"ADMIN"}
                                """))
                .andExpect(status().isOk());

        verify(userService).linkMicrosoftIdentity("tenant", "object", "ana@example.com", "secreta");
    }

    @Test
    void registroFuerzaRolUsuarioYNoDevuelvePassword() throws Exception {
                when(userService.guardarUsuario(any(User.class))).thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        user.setRol("USUARIO");
                        user.setId(1L);
                        return user;
                });

        mockMvc.perform(post("/api/usuarios")
                        .contentType("application/json")
                        .content("""
                                {"nombre":"Ana","email":"ana@example.com","password":"secreta","rol":"ADMIN"}
                                """))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.rol").value("USUARIO"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.password").doesNotExist());
    }

    @Test
    void issuerInvalidoEsRechazado() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig()
                .entraJwtValidator(ISSUER, AUDIENCE);

        Jwt token = token("https://login.microsoftonline.com/14cc35b0-80bc-4ffd-b90a-1fc02097a113/v2.0", AUDIENCE);

        assertTrue(validator.validate(token).hasErrors());
    }

    @Test
    void audienceInvalidoEsRechazado() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig()
                .entraJwtValidator(ISSUER, AUDIENCE);

        Jwt token = token(ISSUER, "api://otra-api");

        assertTrue(validator.validate(token).hasErrors());
    }

    @Test
        void issuerYAudienceValidosSonAceptadosPorElValidadorDeClaims() {
        OAuth2TokenValidator<Jwt> validator = new SecurityConfig()
                .entraJwtValidator(ISSUER, AUDIENCE);

        Jwt token = token(ISSUER, AUDIENCE);

        assertFalse(validator.validate(token).hasErrors());
    }

    private Jwt token(String issuer, String audience) {
        Instant now = Instant.now();
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .issuer(issuer)
                .audience(List.of(audience))
                .issuedAt(now.minusSeconds(30))
                .expiresAt(now.plusSeconds(300))
                .build();
    }
}
