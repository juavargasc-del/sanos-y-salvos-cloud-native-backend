package com.sanosysalvos.usuarios.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

        @Bean
        public OAuth2TokenValidator<Jwt> entraJwtValidator(
                        @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
                        @Value("${entra.security.audience}") String audience) {

                OAuth2TokenValidator<Jwt> issuerAndTimeValidator =
                                JwtValidators.createDefaultWithIssuer(issuer);
                OAuth2TokenValidator<Jwt> audienceValidator =
                                new JwtClaimValidator<List<String>>(
                                                "aud",
                                                audiences -> audiences != null && audiences.contains(audience)
                                );

                return token -> {
                        var issuerResult = issuerAndTimeValidator.validate(token);
                        if (issuerResult.hasErrors()) {
                                return issuerResult;
                        }
                        return audienceValidator.validate(token);
                };
        }

        @Bean
        public JwtDecoder jwtDecoder(
                        @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri,
                        OAuth2TokenValidator<Jwt> entraJwtValidator) {

                NimbusJwtDecoder decoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
                decoder.setJwtValidator(entraJwtValidator);
                return decoder;
        }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/usuarios",
                                "/api/usuarios/login",
                                "/api/auth/login"
                        ).permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        .anyRequest().hasAuthority("SCOPE_access_as_user")
                )

                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> { }));

        return http.build();
    }
}