package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.config.JwtService;
import com.sanosysalvos.usuarios.dto.LoginRequestDTO;
import com.sanosysalvos.usuarios.dto.LoginResponse;
import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<User> guardarUsuario(@Valid @RequestBody User user) {

        User nuevoUsuario = userService.guardarUsuario(user);

        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {

        Optional<User> usuario =
                userService.login(request.getEmail(), request.getPassword());

        if (usuario.isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body("Credenciales incorrectas");
        }

        String token =
                jwtService.generarToken(usuario.get().getEmail());

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listarUsuarios() {

        return ResponseEntity.ok(userService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> buscarPorId(@PathVariable Long id) {

        Optional<UserDTO> usuario = userService.buscarPorId(id);

        return usuario
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {

        userService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }
}