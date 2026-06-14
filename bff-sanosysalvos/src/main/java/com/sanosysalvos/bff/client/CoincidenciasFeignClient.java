package com.sanosysalvos.bff.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "coincidenciasFeignClient", url = "${ms.coincidencias.url}")
public interface CoincidenciasFeignClient {

    @GetMapping("/api/coincidencias")
    Object buscarCoincidencias();

    @GetMapping("/api/coincidencias/usuario/{usuarioId}")
    Object buscarCoincidenciasPorUsuario(@PathVariable("usuarioId") Long usuarioId);
}