package com.sanosysalvos.coincidencias.controller;

import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coincidencias")
@RequiredArgsConstructor
@Tag(name = "Coincidencias", description = "Búsqueda de coincidencias entre mascotas perdidas y encontradas")
public class CoincidenciaController {

    private final CoincidenciaService coincidenciaService;

    @GetMapping
    @Operation(summary = "Buscar coincidencias", description = "Calcula coincidencias entre mascotas perdidas y encontradas")
    @ApiResponse(responseCode = "200", description = "Coincidencias obtenidas correctamente")
    public ResponseEntity<List<CoincidenciaDTO>> buscarCoincidencias() {
        return ResponseEntity.ok(coincidenciaService.buscarCoincidencias());
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar coincidencias por usuario", description = "Filtra las coincidencias asociadas a un usuario")
    @ApiResponse(responseCode = "200", description = "Coincidencias del usuario obtenidas correctamente")
    public ResponseEntity<List<CoincidenciaDTO>> buscarCoincidenciasPorUsuario(@Parameter(description = "Identificador del usuario") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(coincidenciaService.buscarCoincidenciasPorUsuario(usuarioId));
    }
}