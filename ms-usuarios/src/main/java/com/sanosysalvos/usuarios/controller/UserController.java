package com.sanosysalvos.usuarios.controller;

import com.sanosysalvos.usuarios.config.JwtService;
import com.sanosysalvos.usuarios.dto.LoginRequestDTO;
import com.sanosysalvos.usuarios.dto.LoginResponse;
import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.dto.CreateUserRequestDTO;
import com.sanosysalvos.usuarios.dto.CurrentUserResponseDTO;
import com.sanosysalvos.usuarios.dto.MicrosoftLinkRequestDTO;
import com.sanosysalvos.usuarios.service.impl.InvalidMicrosoftLinkException;
import com.sanosysalvos.usuarios.service.impl.MicrosoftLinkConflictException;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.service.UserService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService,
                          JwtService jwtService) {

        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "200", description = "Usuario creado correctamente")
    public ResponseEntity<UserDTO> guardarUsuario(@Valid @RequestBody CreateUserRequestDTO request) {

        User user = User.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User nuevoUsuario = userService.guardarUsuario(user);

        return ResponseEntity.ok(UserDTO.builder()
                .id(nuevoUsuario.getId())
                .nombre(nuevoUsuario.getNombre())
                .email(nuevoUsuario.getEmail())
                .rol(nuevoUsuario.getRol())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponseDTO> currentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {

        return ResponseEntity.ok(userService.currentUser(
                requiredClaim(jwt, "tid"),
                requiredClaim(jwt, "oid")
        ));
    }

    @PostMapping("/link-microsoft")
    public ResponseEntity<?> linkMicrosoft(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MicrosoftLinkRequestDTO request
    ) {

        try {
            return ResponseEntity.ok(userService.linkMicrosoftIdentity(
                    requiredClaim(jwt, "tid"),
                    requiredClaim(jwt, "oid"),
                    request.getEmail(),
                    request.getPassword()
            ));
        } catch (InvalidMicrosoftLinkException exception) {
            return ResponseEntity.status(401).body(exception.getMessage());
        } catch (MicrosoftLinkConflictException exception) {
            return ResponseEntity.status(409).body(exception.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario", description = "Valida credenciales y devuelve el token de acceso")
    @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
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

    User user = usuario.get();

    return ResponseEntity.ok(
        LoginResponse.builder()
            .token(token)
            .userId(user.getId())
            .nombre(user.getNombre())
            .email(user.getEmail())
            .rol(user.getRol())
            .build()
    );
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene la lista de usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido correctamente")
    public ResponseEntity<List<UserDTO>> listarUsuarios() {

        return ResponseEntity.ok(userService.listarUsuarios());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar usuario por ID", description = "Busca un usuario por su identificador")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserDTO> buscarPorId(@Parameter(description = "Identificador del usuario") @PathVariable Long id) {

        Optional<UserDTO> usuario = userService.buscarPorId(id);

        return usuario
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su identificador")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente")
    public ResponseEntity<Void> eliminarUsuario(@Parameter(description = "Identificador del usuario") @PathVariable Long id) {

        userService.eliminarUsuario(id);

        return ResponseEntity.noContent().build();
    }

    private String requiredClaim(Jwt jwt, String claimName) {

        String claim = jwt.getClaimAsString(claimName);
        if (claim == null || claim.isBlank()) {
            throw new IllegalArgumentException("El token no contiene la identidad Microsoft requerida");
        }
        return claim;
    }
}