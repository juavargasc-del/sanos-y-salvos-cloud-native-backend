package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.dto.AuthResponseDTO;
import com.sanosysalvos.bff.dto.LoginRequestDTO;
import com.sanosysalvos.bff.dto.UserDTO;
import com.sanosysalvos.bff.service.UsuariosBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UsuariosBffServiceImpl implements UsuariosBffService {

    private final RestTemplate restTemplate;

    @Value("${ms.usuarios.url}")
    private String usuariosUrl;

    @Override
    public UserDTO crearUsuario(UserDTO userDTO) {

        String url = usuariosUrl + "/api/usuarios";

        return restTemplate.postForObject(
                url,
                userDTO,
                UserDTO.class
        );
    }

    @Override
    public Object listarUsuarios() {

        String url = usuariosUrl + "/api/usuarios";

        return restTemplate.getForObject(url, Object.class);
    }

    @Override
    public Object buscarUsuarioPorId(Long id) {

        String url = usuariosUrl + "/api/usuarios/" + id;

        return restTemplate.getForObject(url, Object.class);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {

        String url = usuariosUrl + "/api/usuarios/login";

        return restTemplate.postForObject(
                url,
                request,
                AuthResponseDTO.class
        );
    }
}