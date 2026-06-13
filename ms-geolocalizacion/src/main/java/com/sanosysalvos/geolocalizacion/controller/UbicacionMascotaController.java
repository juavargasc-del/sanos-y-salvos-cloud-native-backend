package com.sanosysalvos.geolocalizacion.controller;

import com.sanosysalvos.geolocalizacion.dto.DistanciaResponseDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;
import com.sanosysalvos.geolocalizacion.service.UbicacionMascotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geolocalizacion")
@RequiredArgsConstructor
public class UbicacionMascotaController {

    private final UbicacionMascotaService ubicacionMascotaService;

    @PostMapping
    public ResponseEntity<UbicacionMascotaDTO> guardarUbicacion(
            @Valid @RequestBody UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO
    ) {
        UbicacionMascotaDTO ubicacionGuardada =
                ubicacionMascotaService.guardarUbicacion(ubicacionMascotaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionGuardada);
    }

    @GetMapping
    public ResponseEntity<List<UbicacionMascotaDTO>> listarUbicaciones() {
        return ResponseEntity.ok(ubicacionMascotaService.listarUbicaciones());
    }

    @GetMapping("/distancia")
    public ResponseEntity<DistanciaResponseDTO> calcularDistancia(
            @RequestParam double lat1,
            @RequestParam double lon1,
            @RequestParam double lat2,
            @RequestParam double lon2
    ) {
        return ResponseEntity.ok(ubicacionMascotaService.calcularDistancia(lat1, lon1, lat2, lon2));
    }

    @GetMapping("/cercanas")
    public ResponseEntity<List<UbicacionMascotaDTO>> buscarUbicacionesCercanas(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radioKm
    ) {
        return ResponseEntity.ok(ubicacionMascotaService.buscarUbicacionesCercanas(lat, lon, radioKm));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<UbicacionMascotaDTO> obtenerUbicacionPorMascota(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(ubicacionMascotaService.obtenerUbicacionPorMascota(mascotaId));
    }
}
