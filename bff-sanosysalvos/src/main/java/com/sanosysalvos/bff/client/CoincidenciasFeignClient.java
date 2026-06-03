package com.sanosysalvos.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "coincidenciasFeignClient", url = "${ms.coincidencias.url}")
public interface CoincidenciasFeignClient {

    @GetMapping("/api/coincidencias")
    Object buscarCoincidencias();
}