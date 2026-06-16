package com.sanosysalvos.usuarios.service.impl;

import com.sanosysalvos.usuarios.dto.UserDTO;
import com.sanosysalvos.usuarios.model.User;
import com.sanosysalvos.usuarios.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void guardarUsuario_debeGuardarUsuarioConRolExistente() {
        User user = crearUser(null, "Ana", "ana@example.com", "secreta", "ADMIN");

        when(passwordEncoder.encode("secreta")).thenReturn("encoded-secreta");
        when(userRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.guardarUsuario(user);

        assertEquals("ADMIN", resultado.getRol());
        assertEquals("encoded-secreta", resultado.getPassword());
        verify(passwordEncoder).encode("secreta");
        verify(userRepository).save(user);
    }

    @Test
    void guardarUsuario_debeAsignarRolUsuarioCuandoRolEsNull() {
        User user = crearUser(null, "Ana", "ana@example.com", "secreta", null);

        when(passwordEncoder.encode("secreta")).thenReturn("encoded-secreta");
        when(userRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.guardarUsuario(user);

        assertEquals("USUARIO", resultado.getRol());
        assertEquals("encoded-secreta", resultado.getPassword());
        verify(passwordEncoder).encode("secreta");
        verify(userRepository).save(user);
    }

    @Test
    void guardarUsuario_debeAsignarRolUsuarioCuandoRolEstaVacio() {
        User user = crearUser(null, "Ana", "ana@example.com", "secreta", "   ");

        when(passwordEncoder.encode("secreta")).thenReturn("encoded-secreta");
        when(userRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.guardarUsuario(user);

        assertEquals("USUARIO", resultado.getRol());
        assertEquals("encoded-secreta", resultado.getPassword());
        verify(passwordEncoder).encode("secreta");
        verify(userRepository).save(user);
    }

    @Test
    void guardarUsuario_debeEncriptarContraseñaAntesDeGuardar() {
        User user = crearUser(null, "Ana", "ana@example.com", "secreta", "ADMIN");

        when(passwordEncoder.encode("secreta")).thenReturn("encoded-secreta");
        when(userRepository.save(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User resultado = userService.guardarUsuario(user);

        assertEquals("encoded-secreta", resultado.getPassword());
        verify(passwordEncoder).encode("secreta");
        verify(userRepository).save(user);
    }

    @Test
    void login_debeRetornarUsuarioCuandoEmailExisteYPasswordCoincide() {
        User user = crearUser(1L, "Ana", "ana@example.com", "encoded-secreta", "USUARIO");

        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secreta", "encoded-secreta")).thenReturn(true);

        Optional<User> resultado = userService.login("ana@example.com", "secreta");

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("ana@example.com", resultado.get().getEmail());
        verify(userRepository).findByEmail("ana@example.com");
        verify(passwordEncoder).matches("secreta", "encoded-secreta");
    }

    @Test
    void login_debeRetornarOptionalVacioCuandoPasswordEsIncorrecta() {
        User user = crearUser(1L, "Ana", "ana@example.com", "encoded-secreta", "USUARIO");

        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrecta", "encoded-secreta")).thenReturn(false);

        Optional<User> resultado = userService.login("ana@example.com", "incorrecta");

        assertFalse(resultado.isPresent());
        verify(userRepository).findByEmail("ana@example.com");
        verify(passwordEncoder).matches("incorrecta", "encoded-secreta");
    }

    @Test
    void login_debeRetornarOptionalVacioCuandoEmailNoExiste() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        Optional<User> resultado = userService.login("noexiste@example.com", "secreta");

        assertFalse(resultado.isPresent());
        verify(userRepository).findByEmail("noexiste@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void listarUsuarios_debeRetornarDtosCorrectamente() {
        User userUno = crearUser(1L, "Ana", "ana@example.com", "encoded-1", "USUARIO");
        User userDos = crearUser(2L, "Luis", "luis@example.com", "encoded-2", "ADMIN");

        when(userRepository.findAll()).thenReturn(List.of(userUno, userDos));

        List<UserDTO> resultado = userService.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("Ana", resultado.get(0).getNombre());
        assertEquals("ana@example.com", resultado.get(0).getEmail());
        assertEquals("USUARIO", resultado.get(0).getRol());
        assertEquals("Luis", resultado.get(1).getNombre());
        assertEquals("luis@example.com", resultado.get(1).getEmail());
        assertEquals("ADMIN", resultado.get(1).getRol());
        verify(userRepository).findAll();
    }

    @Test
    void buscarPorId_debeRetornarDtoCuandoExiste() {
        User user = crearUser(1L, "Ana", "ana@example.com", "encoded-secreta", "USUARIO");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> resultado = userService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Ana", resultado.get().getNombre());
        verify(userRepository).findById(1L);
    }

    @Test
    void buscarPorId_debeRetornarOptionalVacioCuandoNoExiste() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UserDTO> resultado = userService.buscarPorId(99L);

        assertFalse(resultado.isPresent());
        verify(userRepository).findById(99L);
    }

    @Test
    void buscarPorEmail_debeDelegarCorrectamenteAlRepositorio() {
        User user = crearUser(1L, "Ana", "ana@example.com", "encoded-secreta", "USUARIO");

        when(userRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(user));

        Optional<User> resultado = userService.buscarPorEmail("ana@example.com");

        assertTrue(resultado.isPresent());
        assertEquals("ana@example.com", resultado.get().getEmail());
        verify(userRepository).findByEmail("ana@example.com");
    }

    @Test
    void eliminarUsuario_debeInvocarDeleteById() {
        userService.eliminarUsuario(123L);

        verify(userRepository).deleteById(123L);
    }

    private User crearUser(Long id, String nombre, String email, String password, String rol) {
        return User.builder()
                .id(id)
                .nombre(nombre)
                .email(email)
                .password(password)
                .rol(rol)
                .build();
    }
}