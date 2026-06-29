package com.sanosysalvos.mascotas.controller;

import com.sanosysalvos.mascotas.dto.MascotaDTO;
import com.sanosysalvos.mascotas.service.MascotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
@Tag(name = "Mascotas", description = "Gestión de mascotas registradas en el sistema")
public class MascotaController {

    private final MascotaService mascotaService;

    @PostMapping
    @Operation(summary = "Crear mascota", description = "Registra una nueva mascota en el sistema")
    @ApiResponse(responseCode = "201", description = "Mascota creada correctamente")
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO mascotaDTO) {
        MascotaDTO nuevaMascota = mascotaService.crearMascota(mascotaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMascota);
    }

    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Obtiene el listado completo de mascotas")
    @ApiResponse(responseCode = "200", description = "Listado de mascotas obtenido correctamente")
    public ResponseEntity<List<MascotaDTO>> listarMascotas() {
        return ResponseEntity.ok(mascotaService.listarMascotas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar mascota por ID", description = "Busca una mascota por su identificador")
    @ApiResponse(responseCode = "200", description = "Mascota encontrada")
    @ApiResponse(responseCode = "404", description = "Mascota no encontrada")
    public ResponseEntity<MascotaDTO> buscarMascotaPorId(@Parameter(description = "Identificador de la mascota") @PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.buscarMascotaPorId(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar mascotas por estado", description = "Obtiene las mascotas filtradas por estado")
    @ApiResponse(responseCode = "200", description = "Mascotas filtradas correctamente")
    public ResponseEntity<List<MascotaDTO>> listarPorEstado(@Parameter(description = "Estado de la mascota") @PathVariable String estado) {
        return ResponseEntity.ok(mascotaService.listarPorEstado(estado));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar mascotas por usuario", description = "Obtiene las mascotas asociadas a un usuario")
    @ApiResponse(responseCode = "200", description = "Mascotas del usuario obtenidas correctamente")
    public ResponseEntity<List<MascotaDTO>> listarPorUsuarioId(@Parameter(description = "Identificador del usuario") @PathVariable Long usuarioId) {
        return ResponseEntity.ok(mascotaService.listarPorUsuarioId(usuarioId));
    }

    @PutMapping("/{id}")
        @Operation(summary = "Actualizar mascota", description = "Actualiza la información de una mascota existente")
        @ApiResponse(responseCode = "200", description = "Mascota actualizada correctamente")
    public ResponseEntity<MascotaDTO> actualizarMascota(
            @Parameter(description = "Identificador de la mascota") @PathVariable Long id,
            @RequestBody MascotaDTO mascotaDTO
    ) {
        return ResponseEntity.ok(mascotaService.actualizarMascota(id, mascotaDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota por su identificador")
    @ApiResponse(responseCode = "204", description = "Mascota eliminada correctamente")
    public ResponseEntity<Void> eliminarMascota(@Parameter(description = "Identificador de la mascota") @PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}