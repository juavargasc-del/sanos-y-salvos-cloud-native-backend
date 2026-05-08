package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> guardarUsuario(@RequestBody User user) {
        User nuevoUsuario = userService.guardarUsuario(user);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @GetMapping
    public ResponseEntity<List<User>> listarUsuarios() {
        return ResponseEntity.ok(userService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> buscarPorId(@PathVariable Long id) {

        Optional<User> usuario = userService.buscarPorId(id);

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