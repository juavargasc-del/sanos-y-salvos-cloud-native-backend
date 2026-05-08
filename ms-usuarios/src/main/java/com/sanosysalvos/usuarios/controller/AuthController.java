package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.config.JwtService;
import com.sanosysalvos.usuarios.dto.AuthRequestDTO;
import com.sanosysalvos.usuarios.dto.AuthResponseDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService,
                          JwtService jwtService,
                          PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO request) {

        Optional<User> usuario = userService.buscarPorEmail(request.getEmail());

        if (usuario.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        boolean passwordCorrecta = passwordEncoder.matches(
                request.getPassword(),
                usuario.get().getPassword()
        );

        if (!passwordCorrecta) {
            return ResponseEntity.badRequest().body("Contraseña incorrecta");
        }

        String token = jwtService.generarToken(usuario.get().getEmail());

        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}