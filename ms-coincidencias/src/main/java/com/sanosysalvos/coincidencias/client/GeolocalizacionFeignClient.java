package com.sanosysalvos.coincidencias.client;

import com.sanosysalvos.coincidencias.dto.DistanciaResponseDTO;
import com.sanosysalvos.coincidencias.dto.UbicacionMascotaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geolocalizacionFeignClient", url = "${ms.geolocalizacion.url}")
public interface GeolocalizacionFeignClient {

    @GetMapping("/api/geolocalizacion/mascota/{mascotaId}")
    UbicacionMascotaDTO obtenerUbicacionPorMascota(@PathVariable("mascotaId") Long mascotaId);

    @GetMapping("/api/geolocalizacion/distancia")
    DistanciaResponseDTO calcularDistancia(
            @RequestParam("lat1") double lat1,
            @RequestParam("lon1") double lon1,
            @RequestParam("lat2") double lat2,
            @RequestParam("lon2") double lon2
    );
}
