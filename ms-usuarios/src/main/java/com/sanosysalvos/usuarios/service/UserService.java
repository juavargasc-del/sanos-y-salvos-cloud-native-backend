package com.sanosysalvos.usuarios.service;

import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User guardarUsuario(User user);

    List<UserDTO> listarUsuarios();

    Optional<UserDTO> buscarPorId(Long id);

    Optional<User> buscarPorEmail(String email);

    Optional<User> login(String email, String password);

    void eliminarUsuario(Long id);
}