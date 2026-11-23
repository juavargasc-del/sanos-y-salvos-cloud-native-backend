package com.sanosysalvos.rabbitmqadmin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("local-test")
public class LocalTestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService localAdminUser(
            PasswordEncoder passwordEncoder,
            @Value("${rabbitmq.admin.local.username}") String username,
            @Value("${rabbitmq.admin.local.password}") String password,
            @Value("${rabbitmq.admin.authority}") String adminAuthority) {
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .authorities(adminAuthority)
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public SecurityFilterChain localSecurityFilterChain(
            HttpSecurity http,
            @Value("${rabbitmq.admin.authority}") String adminAuthority)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error", "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority(adminAuthority)
                        .anyRequest().denyAll())
                .httpBasic(httpBasic -> {})
                .formLogin(form -> form.disable());
        return http.build();
    }
}
