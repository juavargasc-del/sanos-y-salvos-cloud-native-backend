package com.sanosysalvos.usuarios.service.impl;

import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.dto.CurrentUserResponseDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.repository.UserRepository;
import com.sanosysalvos.usuarios.service.UserService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User guardarUsuario(User user) {

        user.setRol("USUARIO");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String email, String password) {

        Optional<User> usuario = userRepository.findByEmail(email);

        if (usuario.isPresent()) {

            boolean passwordCorrecta =
                    passwordEncoder.matches(password, usuario.get().getPassword());

            if (passwordCorrecta) {
                return usuario;
            }
        }

        return Optional.empty();
    }

    @Override
    public List<UserDTO> listarUsuarios() {

        return userRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public Optional<UserDTO> buscarPorId(Long id) {

        return userRepository.findById(id)
                .map(this::convertirADTO);
    }

    @Override
    public Optional<User> buscarPorEmail(String email) {

        return userRepository.findByEmail(email);
    }

    @Override
    public CurrentUserResponseDTO currentUser(String externalTenantId, String externalObjectId) {

        return userRepository.findByExternalTenantIdAndExternalObjectId(
                        externalTenantId,
                        externalObjectId
                )
                .map(this::convertirAResponseVinculado)
                .orElseGet(() -> CurrentUserResponseDTO.builder()
                        .linked(false)
                        .message("La cuenta Microsoft debe vincularse con un usuario local")
                        .build());
    }

    @Override
    public CurrentUserResponseDTO linkMicrosoftIdentity(
            String externalTenantId,
            String externalObjectId,
            String email,
            String password
    ) {

        Optional<User> identityOwner = userRepository
                .findByExternalTenantIdAndExternalObjectId(externalTenantId, externalObjectId);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidMicrosoftLinkException("Credenciales locales incorrectas"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidMicrosoftLinkException("Credenciales locales incorrectas");
        }

        if (identityOwner.isPresent()) {
            if (identityOwner.get().getId().equals(user.getId())) {
                throw new MicrosoftLinkConflictException("La identidad Microsoft ya está vinculada a este usuario");
            }
            throw new MicrosoftLinkConflictException("La identidad Microsoft ya está vinculada a otro usuario");
        }

        if (user.getExternalTenantId() != null || user.getExternalObjectId() != null) {
            throw new MicrosoftLinkConflictException("El usuario local ya está vinculado a otra identidad Microsoft");
        }

        user.setExternalTenantId(externalTenantId);
        user.setExternalObjectId(externalObjectId);

        try {
            return convertirAResponseVinculado(userRepository.save(user));
        } catch (DataIntegrityViolationException exception) {
            throw new MicrosoftLinkConflictException("La identidad Microsoft ya está vinculada a otro usuario");
        }
    }

    @Override
    public void eliminarUsuario(Long id) {

        userRepository.deleteById(id);
    }

    private UserDTO convertirADTO(User user) {

        return UserDTO.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRol())
                .build();
    }

    private CurrentUserResponseDTO convertirAResponseVinculado(User user) {

        return CurrentUserResponseDTO.builder()
                .linked(true)
                .userId(user.getId())
                .nombre(user.getNombre())
                .email(user.getEmail())
                .rol(user.getRol())
                .build();
    }
}