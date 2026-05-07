package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.dto.MascotaDTO;
import com.sanosysalvos.bff.service.MascotasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/mascotas")
@RequiredArgsConstructor
public class MascotasBffController {

    private final MascotasBffService mascotasBffService;

    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO mascotaDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mascotasBffService.crearMascota(mascotaDTO));
    }

    @GetMapping
    public ResponseEntity<Object> listarMascotas() {
        return ResponseEntity.ok(mascotasBffService.listarMascotas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarMascotaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotasBffService.buscarMascotaPorId(id));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<Object> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(mascotasBffService.listarPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarMascota(
            @PathVariable Long id,
            @RequestBody MascotaDTO mascotaDTO
    ) {
        return ResponseEntity.ok(
                mascotasBffService.actualizarMascota(id, mascotaDTO)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotasBffService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}