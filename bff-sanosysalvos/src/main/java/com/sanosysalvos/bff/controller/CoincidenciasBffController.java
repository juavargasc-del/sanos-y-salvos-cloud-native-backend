package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.service.CoincidenciasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/coincidencias")
@RequiredArgsConstructor
public class CoincidenciasBffController {

    private final CoincidenciasBffService coincidenciasBffService;

    @GetMapping
    public ResponseEntity<Object> buscarCoincidencias() {
        return ResponseEntity.ok(coincidenciasBffService.buscarCoincidencias());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Object> buscarCoincidenciasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(coincidenciasBffService.buscarCoincidenciasPorUsuario(usuarioId));
    }
}