package com.sanosysalvos.coincidencias.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignJwtRequestInterceptor {

    @Bean
    public RequestInterceptor authorizationRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

                if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
                    return;
                }

                HttpServletRequest request = servletRequestAttributes.getRequest();
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null && !authorizationHeader.isBlank()) {
                    template.header("Authorization", authorizationHeader);
                }
            }
        };
    }
}