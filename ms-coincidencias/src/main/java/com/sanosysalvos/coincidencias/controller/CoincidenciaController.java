package com.sanosysalvos.coincidencias.controller;

import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coincidencias")
@RequiredArgsConstructor
public class CoincidenciaController {

    private final CoincidenciaService coincidenciaService;

    @GetMapping
    public ResponseEntity<List<CoincidenciaDTO>> buscarCoincidencias() {
        return ResponseEntity.ok(coincidenciaService.buscarCoincidencias());
    }
}