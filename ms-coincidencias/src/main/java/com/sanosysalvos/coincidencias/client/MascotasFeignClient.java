package com.sanosysalvos.coincidencias.client;

import com.sanosysalvos.coincidencias.dto.MascotaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "mascotasFeignClient", url = "${ms.mascotas.url}")
public interface MascotasFeignClient {

    @GetMapping("/api/mascotas")
    MascotaDTO[] listarMascotas();
}