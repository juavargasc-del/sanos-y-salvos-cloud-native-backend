package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.client.UsuariosFeignClient;
import com.sanosysalvos.bff.dto.AuthResponseDTO;
import com.sanosysalvos.bff.dto.LoginRequestDTO;
import com.sanosysalvos.bff.dto.UserDTO;
import com.sanosysalvos.bff.service.UsuariosBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuariosBffServiceImpl implements UsuariosBffService {

    private final UsuariosFeignClient usuariosFeignClient;

    @Override
    public UserDTO crearUsuario(UserDTO userDTO) {

        return usuariosFeignClient.crearUsuario(userDTO);
    }

    @Override
    public Object listarUsuarios(String token) {

        return usuariosFeignClient.listarUsuarios(token);
    }

    @Override
    public Object buscarUsuarioPorId(Long id, String token) {

        return usuariosFeignClient.buscarUsuarioPorId(id, token);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        return usuariosFeignClient.login(request);
    }
}