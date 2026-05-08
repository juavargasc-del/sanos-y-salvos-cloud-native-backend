package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.dto.AuthResponseDTO;
import com.sanosysalvos.bff.dto.LoginRequestDTO;
import com.sanosysalvos.bff.dto.UserDTO;

public interface UsuariosBffService {

    UserDTO crearUsuario(UserDTO userDTO);

    Object listarUsuarios();

    Object buscarUsuarioPorId(Long id);

    AuthResponseDTO login(LoginRequestDTO request);
}