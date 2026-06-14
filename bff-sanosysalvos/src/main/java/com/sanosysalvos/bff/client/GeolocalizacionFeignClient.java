package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.dto.DistanciaResponseDTO;
import com.sanosysalvos.bff.dto.UbicacionMascotaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geolocalizacionFeignClient", url = "${ms.geolocalizacion.url}")
public interface GeolocalizacionFeignClient {

    @PostMapping("/api/geolocalizacion")
    UbicacionMascotaDTO guardarUbicacion(@RequestBody UbicacionMascotaDTO ubicacionMascotaDTO);

    @GetMapping("/api/geolocalizacion")
    Object listarUbicaciones();

    @GetMapping("/api/geolocalizacion/mascota/{mascotaId}")
    Object obtenerUbicacionPorMascota(@PathVariable("mascotaId") Long mascotaId);

    @GetMapping("/api/geolocalizacion/distancia")
    DistanciaResponseDTO calcularDistancia(
            @RequestParam("lat1") double lat1,
            @RequestParam("lon1") double lon1,
            @RequestParam("lat2") double lat2,
            @RequestParam("lon2") double lon2
    );

    @GetMapping("/api/geolocalizacion/cercanas")
    Object buscarUbicacionesCercanas(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon,
            @RequestParam("radioKm") double radioKm
    );
}
