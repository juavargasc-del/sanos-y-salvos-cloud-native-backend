package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.dto.LoginRequestDTO;
import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder) {

        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public ResponseEntity<User> guardarUsuario(@Valid @RequestBody User user) {

        User nuevoUsuario = userService.guardarUsuario(user);

        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {

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

        return ResponseEntity.ok("Login exitoso");
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