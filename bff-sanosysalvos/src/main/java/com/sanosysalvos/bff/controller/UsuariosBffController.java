package com.sanosysalvos.bff.controller;

import com.sanosysalvos.bff.dto.AuthResponseDTO;
import com.sanosysalvos.bff.dto.LoginRequestDTO;
import com.sanosysalvos.bff.dto.UserDTO;
import com.sanosysalvos.bff.service.UsuariosBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/usuarios")
@RequiredArgsConstructor
public class UsuariosBffController {

    private final UsuariosBffService usuariosBffService;

    @PostMapping
    public ResponseEntity<UserDTO> crearUsuario(
            @RequestBody UserDTO userDTO
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuariosBffService.crearUsuario(userDTO));
    }

    @GetMapping
    public ResponseEntity<Object> listarUsuarios(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {

        return ResponseEntity.ok(
                usuariosBffService.listarUsuarios(token)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUsuarioPorId(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) {

        return ResponseEntity.ok(
                usuariosBffService.buscarUsuarioPorId(id, token)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {

        return ResponseEntity.ok(
                usuariosBffService.login(request)
        );
    }
}