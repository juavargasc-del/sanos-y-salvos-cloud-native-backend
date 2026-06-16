package com.sanosysalvos.mascotas.service.impl;

import com.sanosysalvos.mascotas.dto.MascotaDTO;
import com.sanosysalvos.mascotas.model.Mascota;
import com.sanosysalvos.mascotas.repository.MascotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    @Test
    void crearMascota_debeGuardarCorrectamenteYRetornarDtoGenerado() {
        MascotaDTO mascotaDTO = crearMascotaDTO(null, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");

        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> {
            Mascota mascota = invocation.getArgument(0);
            mascota.setId(1L);
            mascota.setFechaReporte(LocalDateTime.of(2026, 1, 1, 10, 0));
            return mascota;
        });

        MascotaDTO resultado = mascotaService.crearMascota(mascotaDTO);

        assertEquals("Luna", resultado.getNombre());
        assertEquals("Perro", resultado.getTipo());
        assertEquals("Labrador", resultado.getRaza());
        assertEquals("ENCONTRADA", resultado.getEstado());
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void listarMascotas_debeRetornarLaCantidadYCContenidoEsperado() {
        Mascota mascotaUno = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");
        Mascota mascotaDos = crearMascotaEntidad(2L, "Michi", "Gato", "Siames", "Blanco", 2, "Pequena", 20L, "PERDIDA");

        when(mascotaRepository.findAll()).thenReturn(List.of(mascotaUno, mascotaDos));

        List<MascotaDTO> resultado = mascotaService.listarMascotas();

        assertEquals(2, resultado.size());
        assertEquals("Luna", resultado.get(0).getNombre());
        assertEquals("Michi", resultado.get(1).getNombre());
        verify(mascotaRepository).findAll();
    }

    @Test
    void buscarMascotaPorId_debeRetornarMascotaCuandoExiste() {
        Mascota mascota = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        MascotaDTO resultado = mascotaService.buscarMascotaPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("Luna", resultado.getNombre());
        verify(mascotaRepository).findById(1L);
    }

    @Test
    void buscarMascotaPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mascotaService.buscarMascotaPorId(99L));

        assertEquals("Mascota no encontrada con id: 99", exception.getMessage());
        verify(mascotaRepository).findById(99L);
    }

    @Test
    void listarPorEstado_debeRetornarListaFiltradaConEstadoCorrecto() {
        Mascota mascotaUno = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");
        Mascota mascotaDos = crearMascotaEntidad(2L, "Max", "Perro", "Pastor", "Cafe", 5, "Mediana", 11L, "ENCONTRADA");

        when(mascotaRepository.findByEstado("ENCONTRADA")).thenReturn(List.of(mascotaUno, mascotaDos));

        List<MascotaDTO> resultado = mascotaService.listarPorEstado("ENCONTRADA");

        assertEquals(2, resultado.size());
        assertEquals("ENCONTRADA", resultado.get(0).getEstado());
        assertEquals("ENCONTRADA", resultado.get(1).getEstado());
        verify(mascotaRepository).findByEstado("ENCONTRADA");
    }

    @Test
    void listarPorUsuarioId_debeRetornarListaFiltradaConUsuarioIdCorrecto() {
        Mascota mascotaUno = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");
        Mascota mascotaDos = crearMascotaEntidad(2L, "Max", "Perro", "Pastor", "Cafe", 5, "Mediana", 10L, "PERDIDA");

        when(mascotaRepository.findByUsuarioId(10L)).thenReturn(List.of(mascotaUno, mascotaDos));

        List<MascotaDTO> resultado = mascotaService.listarPorUsuarioId(10L);

        assertEquals(2, resultado.size());
        assertEquals(10L, resultado.get(0).getUsuarioId());
        assertEquals(10L, resultado.get(1).getUsuarioId());
        verify(mascotaRepository).findByUsuarioId(10L);
    }

    @Test
    void actualizarMascota_debeActualizarCamposYGuardarUnaVez() {
        Mascota mascotaExistente = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");
        MascotaDTO mascotaActualizada = crearMascotaDTO(1L, "Luna Nueva", "Perro", "Golden", "Negro", 4, "Grande", 10L, "PERDIDA");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascotaExistente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MascotaDTO resultado = mascotaService.actualizarMascota(1L, mascotaActualizada);

        assertEquals("Luna Nueva", resultado.getNombre());
        assertEquals("Golden", resultado.getRaza());
        assertEquals("PERDIDA", resultado.getEstado());
        verify(mascotaRepository).save(any(Mascota.class));
    }

    @Test
    void actualizarMascota_debeLanzarExcepcionCuandoNoExiste() {
        MascotaDTO mascotaActualizada = crearMascotaDTO(null, "Luna Nueva", "Perro", "Golden", "Negro", 4, "Grande", 10L, "PERDIDA");

        when(mascotaRepository.findById(404L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mascotaService.actualizarMascota(404L, mascotaActualizada));

        assertEquals("Mascota no encontrada con id: 404", exception.getMessage());
        verify(mascotaRepository).findById(404L);
    }

    @Test
    void eliminarMascota_debeInvocarDeleteUnaVezCuandoExiste() {
        Mascota mascota = crearMascotaEntidad(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", 10L, "ENCONTRADA");

        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        mascotaService.eliminarMascota(1L);

        verify(mascotaRepository).delete(mascota);
    }

    @Test
    void eliminarMascota_debeLanzarExcepcionCuandoNoExiste() {
        when(mascotaRepository.findById(500L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> mascotaService.eliminarMascota(500L));

        assertEquals("Mascota no encontrada con id: 500", exception.getMessage());
        verify(mascotaRepository).findById(500L);
    }

    private MascotaDTO crearMascotaDTO(Long id, String nombre, String tipo, String raza, String color, Integer edad, String dimension, Long usuarioId, String estado) {
        return MascotaDTO.builder()
                .id(id)
                .nombre(nombre)
                .tipo(tipo)
                .raza(raza)
                .color(color)
                .edad(edad)
                .dimension(dimension)
                .usuarioId(usuarioId)
                .fotoBase64("foto-base64")
                .fechaReporte(LocalDateTime.of(2025, 1, 1, 12, 0))
                .estado(estado)
                .build();
    }

    private Mascota crearMascotaEntidad(Long id, String nombre, String tipo, String raza, String color, Integer edad, String dimension, Long usuarioId, String estado) {
        return Mascota.builder()
                .id(id)
                .nombre(nombre)
                .tipo(tipo)
                .raza(raza)
                .color(color)
                .edad(edad)
                .dimension(dimension)
                .usuarioId(usuarioId)
                .fotoBase64("foto-base64")
                .fechaReporte(LocalDateTime.of(2025, 1, 1, 12, 0))
                .estado(estado)
                .build();
    }
}