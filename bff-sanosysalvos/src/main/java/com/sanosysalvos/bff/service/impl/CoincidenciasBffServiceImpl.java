package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.service.CoincidenciasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CoincidenciasBffServiceImpl implements CoincidenciasBffService {

    private final RestTemplate restTemplate;

    @Value("${ms.coincidencias.url}")
    private String coincidenciasUrl;

    @Override
    public Object buscarCoincidencias() {
        String url = coincidenciasUrl + "/api/coincidencias";
        return restTemplate.getForObject(url, Object.class);
    }
}