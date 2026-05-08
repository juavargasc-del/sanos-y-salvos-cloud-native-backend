package com.sanosysalvos.usuarios.service.impl;

import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.repository.UserRepository;
import com.sanosysalvos.usuarios.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User guardarUsuario(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> listarUsuarios() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void eliminarUsuario(Long id) {
        userRepository.deleteById(id);
    }
}