package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.dto.DistanciaResponseDTO;
import com.sanosysalvos.bff.dto.UbicacionMascotaDTO;
import com.sanosysalvos.bff.service.GeolocalizacionBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bff/geolocalizacion")
@RequiredArgsConstructor
public class GeolocalizacionBffController {

    private final GeolocalizacionBffService geolocalizacionBffService;

    @PostMapping
    public ResponseEntity<UbicacionMascotaDTO> guardarUbicacion(
            @RequestBody UbicacionMascotaDTO ubicacionMascotaDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(geolocalizacionBffService.guardarUbicacion(ubicacionMascotaDTO));
    }

    @GetMapping
    public ResponseEntity<Object> listarUbicaciones() {
        return ResponseEntity.ok(geolocalizacionBffService.listarUbicaciones());
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<Object> obtenerUbicacionPorMascota(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(geolocalizacionBffService.obtenerUbicacionPorMascota(mascotaId));
    }

    @GetMapping("/distancia")
    public ResponseEntity<DistanciaResponseDTO> calcularDistancia(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2
    ) {
        return ResponseEntity.ok(
                geolocalizacionBffService.calcularDistancia(lat1, lon1, lat2, lon2)
        );
    }

    @GetMapping("/cercanas")
    public ResponseEntity<Object> buscarUbicacionesCercanas(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radioKm
    ) {
        return ResponseEntity.ok(
                geolocalizacionBffService.buscarUbicacionesCercanas(lat, lon, radioKm)
        );
    }
}
