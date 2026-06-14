package com.sanosysalvos.mascotas.controller;

import com.sanosysalvos.mascotas.dto.MascotaDTO;
import com.sanosysalvos.mascotas.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO mascotaDTO) {
        MascotaDTO nuevaMascota = mascotaService.crearMascota(mascotaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMascota);
    }

    @GetMapping
    public ResponseEntity<List<MascotaDTO>> listarMascotas() {
        return ResponseEntity.ok(mascotaService.listarMascotas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> buscarMascotaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.buscarMascotaPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MascotaDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(mascotaService.listarPorEstado(estado));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<MascotaDTO>> listarPorUsuarioId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(mascotaService.listarPorUsuarioId(usuarioId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> actualizarMascota(
            @PathVariable Long id,
            @RequestBody MascotaDTO mascotaDTO
    ) {
        return ResponseEntity.ok(mascotaService.actualizarMascota(id, mascotaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}