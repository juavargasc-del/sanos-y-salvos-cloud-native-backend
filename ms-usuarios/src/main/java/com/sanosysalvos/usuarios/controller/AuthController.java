package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.config.JwtService;
import com.sanosysalvos.usuarios.dto.AuthRequestDTO;
import com.sanosysalvos.usuarios.dto.AuthResponseDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Operaciones de inicio de sesión del microservicio de usuarios")
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
    @Operation(summary = "Iniciar sesión", description = "Valida las credenciales del usuario y devuelve un token JWT")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "400", description = "Usuario no encontrado o contraseña incorrecta")
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

        User user = usuario.get();

        return ResponseEntity.ok(
            AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRol())
                .build()
        );
    }
}