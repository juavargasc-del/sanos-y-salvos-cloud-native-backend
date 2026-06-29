package com.sanosysalvos.geolocalizacion.controller;

import com.sanosysalvos.geolocalizacion.dto.DistanciaResponseDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;
import com.sanosysalvos.geolocalizacion.service.UbicacionMascotaService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Geolocalización", description = "Gestión de ubicaciones y cálculo de distancias de mascotas")
public class UbicacionMascotaController {

    private final UbicacionMascotaService ubicacionMascotaService;

    @PostMapping
        @Operation(summary = "Guardar ubicación", description = "Registra la ubicación de una mascota")
        @ApiResponse(responseCode = "201", description = "Ubicación guardada correctamente")
    public ResponseEntity<UbicacionMascotaDTO> guardarUbicacion(
            @Valid @RequestBody UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO
    ) {
        UbicacionMascotaDTO ubicacionGuardada =
                ubicacionMascotaService.guardarUbicacion(ubicacionMascotaRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ubicacionGuardada);
    }

    @GetMapping
    @Operation(summary = "Listar ubicaciones", description = "Obtiene todas las ubicaciones de mascotas registradas")
    @ApiResponse(responseCode = "200", description = "Listado de ubicaciones obtenido correctamente")
    public ResponseEntity<List<UbicacionMascotaDTO>> listarUbicaciones() {
        return ResponseEntity.ok(ubicacionMascotaService.listarUbicaciones());
    }

    @GetMapping("/distancia")
        @Operation(summary = "Calcular distancia", description = "Calcula la distancia entre dos ubicaciones geográficas")
        @ApiResponse(responseCode = "200", description = "Distancia calculada correctamente")
    public ResponseEntity<DistanciaResponseDTO> calcularDistancia(
            @Parameter(description = "Latitud del primer punto") @RequestParam double lat1,
            @Parameter(description = "Longitud del primer punto") @RequestParam double lon1,
            @Parameter(description = "Latitud del segundo punto") @RequestParam double lat2,
            @Parameter(description = "Longitud del segundo punto") @RequestParam double lon2
    ) {
        return ResponseEntity.ok(ubicacionMascotaService.calcularDistancia(lat1, lon1, lat2, lon2));
    }

    @GetMapping("/cercanas")
        @Operation(summary = "Buscar ubicaciones cercanas", description = "Obtiene las ubicaciones dentro de un radio en kilómetros")
        @ApiResponse(responseCode = "200", description = "Ubicaciones cercanas obtenidas correctamente")
    public ResponseEntity<List<UbicacionMascotaDTO>> buscarUbicacionesCercanas(
            @Parameter(description = "Latitud de referencia") @RequestParam double lat,
            @Parameter(description = "Longitud de referencia") @RequestParam double lon,
            @Parameter(description = "Radio de búsqueda en kilómetros") @RequestParam double radioKm
    ) {
        return ResponseEntity.ok(ubicacionMascotaService.buscarUbicacionesCercanas(lat, lon, radioKm));
    }

    @GetMapping("/mascota/{mascotaId}")
    @Operation(summary = "Consultar ubicación por mascota", description = "Obtiene la ubicación registrada para una mascota")
    @ApiResponse(responseCode = "200", description = "Ubicación encontrada correctamente")
    @ApiResponse(responseCode = "404", description = "No existe ubicación para la mascota")
    public ResponseEntity<UbicacionMascotaDTO> obtenerUbicacionPorMascota(@Parameter(description = "Identificador de la mascota") @PathVariable Long mascotaId) {
        return ResponseEntity.ok(ubicacionMascotaService.obtenerUbicacionPorMascota(mascotaId));
    }
}
