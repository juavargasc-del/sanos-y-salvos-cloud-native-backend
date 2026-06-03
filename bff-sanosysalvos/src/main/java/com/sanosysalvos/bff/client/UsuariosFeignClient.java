package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.dto.AuthResponseDTO;
import com.sanosysalvos.bff.dto.LoginRequestDTO;
import com.sanosysalvos.bff.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "usuariosFeignClient", url = "${ms.usuarios.url}")
public interface UsuariosFeignClient {

    @PostMapping("/api/usuarios")
    UserDTO crearUsuario(@RequestBody UserDTO userDTO);

    @GetMapping("/api/usuarios")
    Object listarUsuarios(@RequestHeader("Authorization") String token);

    @GetMapping("/api/usuarios/{id}")
    Object buscarUsuarioPorId(@PathVariable("id") Long id, @RequestHeader("Authorization") String token);

    @PostMapping("/api/usuarios/login")
    AuthResponseDTO login(@RequestBody LoginRequestDTO request);
}