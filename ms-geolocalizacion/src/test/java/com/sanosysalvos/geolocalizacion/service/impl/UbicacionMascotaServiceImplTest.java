package com.sanosysalvos.geolocalizacion.service.impl;

import com.sanosysalvos.geolocalizacion.dto.DistanciaResponseDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;
import com.sanosysalvos.geolocalizacion.model.UbicacionMascota;
import com.sanosysalvos.geolocalizacion.repository.UbicacionMascotaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UbicacionMascotaServiceImplTest {

    @Mock
    private UbicacionMascotaRepository ubicacionMascotaRepository;

    @InjectMocks
    private UbicacionMascotaServiceImpl ubicacionMascotaService;

    @Test
    void guardarUbicacion_debeGuardarCorrectamenteYRetornarDto() {
        LocalDateTime fechaRegistro = LocalDateTime.of(2026, 1, 15, 10, 30);
        UbicacionMascotaRequestDTO requestDTO = crearRequest(10L, 11.11, 22.22, fechaRegistro);

        when(ubicacionMascotaRepository.save(org.mockito.ArgumentMatchers.any(UbicacionMascota.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UbicacionMascotaDTO resultado = ubicacionMascotaService.guardarUbicacion(requestDTO);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getMascotaId());
        assertEquals(11.11, resultado.getLatitud());
        assertEquals(22.22, resultado.getLongitud());
        assertEquals(fechaRegistro, resultado.getFechaRegistro());
        verify(ubicacionMascotaRepository).save(org.mockito.ArgumentMatchers.any(UbicacionMascota.class));
    }

    @Test
    void guardarUbicacion_debeUsarFechaRegistroDelRequest() {
        LocalDateTime fechaRegistro = LocalDateTime.of(2026, 2, 1, 8, 0);
        UbicacionMascotaRequestDTO requestDTO = crearRequest(20L, 12.12, 23.23, fechaRegistro);

        ArgumentCaptor<UbicacionMascota> captor = ArgumentCaptor.forClass(UbicacionMascota.class);
        when(ubicacionMascotaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        UbicacionMascotaDTO resultado = ubicacionMascotaService.guardarUbicacion(requestDTO);

        assertEquals(fechaRegistro, captor.getValue().getFechaRegistro());
        assertEquals(fechaRegistro, resultado.getFechaRegistro());
        verify(ubicacionMascotaRepository).save(captor.getValue());
    }

    @Test
    void guardarUbicacion_debeAsignarFechaActualCuandoFechaRegistroEsNull() {
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        UbicacionMascotaRequestDTO requestDTO = crearRequest(30L, 13.13, 24.24, null);

        ArgumentCaptor<UbicacionMascota> captor = ArgumentCaptor.forClass(UbicacionMascota.class);
        when(ubicacionMascotaRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        UbicacionMascotaDTO resultado = ubicacionMascotaService.guardarUbicacion(requestDTO);
        LocalDateTime despues = LocalDateTime.now().plusSeconds(1);

        assertNotNull(captor.getValue().getFechaRegistro());
        assertTrue(!captor.getValue().getFechaRegistro().isBefore(antes));
        assertTrue(!captor.getValue().getFechaRegistro().isAfter(despues));
        assertEquals(captor.getValue().getFechaRegistro(), resultado.getFechaRegistro());
        verify(ubicacionMascotaRepository).save(captor.getValue());
    }

    @Test
    void listarUbicaciones_debeRetornarListaDeDtosCorrectamente() {
        UbicacionMascota ubicacionUno = crearEntidad(1L, 100L, 1.1, 2.2, LocalDateTime.of(2026, 3, 1, 10, 0));
        UbicacionMascota ubicacionDos = crearEntidad(2L, 200L, 3.3, 4.4, LocalDateTime.of(2026, 3, 2, 10, 0));

        when(ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()).thenReturn(List.of(ubicacionUno, ubicacionDos));

        List<UbicacionMascotaDTO> resultado = ubicacionMascotaService.listarUbicaciones();

        assertEquals(2, resultado.size());
        assertEquals(100L, resultado.get(0).getMascotaId());
        assertEquals(1.1, resultado.get(0).getLatitud());
        assertEquals(2.2, resultado.get(0).getLongitud());
        assertEquals(200L, resultado.get(1).getMascotaId());
        verify(ubicacionMascotaRepository).findAllByOrderByFechaRegistroDesc();
    }

    @Test
    void listarUbicaciones_debeRetornarListaVaciaCuandoNoHayRegistros() {
        when(ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()).thenReturn(List.of());

        List<UbicacionMascotaDTO> resultado = ubicacionMascotaService.listarUbicaciones();

        assertTrue(resultado.isEmpty());
        verify(ubicacionMascotaRepository).findAllByOrderByFechaRegistroDesc();
    }

    @Test
    void obtenerUbicacionPorMascota_debeRetornarUbicacionCuandoExiste() {
        LocalDateTime fechaRegistro = LocalDateTime.of(2026, 4, 10, 12, 0);
        UbicacionMascota ubicacion = crearEntidad(5L, 500L, 5.5, 6.6, fechaRegistro);

        when(ubicacionMascotaRepository.findTopByMascotaIdOrderByFechaRegistroDesc(500L)).thenReturn(Optional.of(ubicacion));

        UbicacionMascotaDTO resultado = ubicacionMascotaService.obtenerUbicacionPorMascota(500L);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals(500L, resultado.getMascotaId());
        assertEquals(5.5, resultado.getLatitud());
        assertEquals(6.6, resultado.getLongitud());
        assertEquals(fechaRegistro, resultado.getFechaRegistro());
        verify(ubicacionMascotaRepository).findTopByMascotaIdOrderByFechaRegistroDesc(500L);
    }

    @Test
    void obtenerUbicacionPorMascota_debeLanzarExcepcionCuandoNoExisteUbicacion() {
        when(ubicacionMascotaRepository.findTopByMascotaIdOrderByFechaRegistroDesc(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> ubicacionMascotaService.obtenerUbicacionPorMascota(999L));

        assertEquals("Ubicacion no encontrada para la mascota con id: 999", exception.getMessage());
        verify(ubicacionMascotaRepository).findTopByMascotaIdOrderByFechaRegistroDesc(999L);
    }

    @Test
    void calcularDistancia_debeRetornarUnDistanciaResponseDtoValido() {
        DistanciaResponseDTO resultado = ubicacionMascotaService.calcularDistancia(10.0, 20.0, 10.5, 20.5);

        assertNotNull(resultado);
        assertNotNull(resultado.getDistanciaKm());
        assertTrue(resultado.getDistanciaKm() > 0);
    }

    @Test
    void calcularDistancia_debeRetornarUnaDistanciaMayorQueCeroParaCoordenadasDistintas() {
        DistanciaResponseDTO resultado = ubicacionMascotaService.calcularDistancia(0.0, 0.0, 1.0, 1.0);

        assertTrue(resultado.getDistanciaKm() > 0);
    }

    @Test
    void buscarUbicacionesCercanas_debeRetornarSoloUbicacionesDentroDelRadioIndicado() {
        UbicacionMascota cercana = crearEntidad(1L, 100L, 10.0, 10.0, LocalDateTime.of(2026, 5, 1, 10, 0));
        UbicacionMascota lejana = crearEntidad(2L, 200L, 20.0, 20.0, LocalDateTime.of(2026, 5, 2, 10, 0));

        when(ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()).thenReturn(List.of(cercana, lejana));

        List<UbicacionMascotaDTO> resultado = ubicacionMascotaService.buscarUbicacionesCercanas(10.0, 10.0, 5.0);

        assertEquals(1, resultado.size());
        assertEquals(100L, resultado.get(0).getMascotaId());
        verify(ubicacionMascotaRepository).findAllByOrderByFechaRegistroDesc();
    }

    @Test
    void buscarUbicacionesCercanas_debeRetornarListaVaciaCuandoNingunaUbicacionCumpleElRadio() {
        UbicacionMascota lejanaUno = crearEntidad(1L, 300L, 50.0, 50.0, LocalDateTime.of(2026, 6, 1, 10, 0));
        UbicacionMascota lejanaDos = crearEntidad(2L, 400L, 60.0, 60.0, LocalDateTime.of(2026, 6, 2, 10, 0));

        when(ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()).thenReturn(List.of(lejanaUno, lejanaDos));

        List<UbicacionMascotaDTO> resultado = ubicacionMascotaService.buscarUbicacionesCercanas(10.0, 10.0, 1.0);

        assertTrue(resultado.isEmpty());
        verify(ubicacionMascotaRepository).findAllByOrderByFechaRegistroDesc();
    }

    @Test
    void convertirEntidadADto_debeConservarCamposPrincipales() {
        LocalDateTime fechaRegistro = LocalDateTime.of(2026, 7, 1, 9, 15);
        UbicacionMascota ubicacion = crearEntidad(77L, 888L, 7.7, 8.8, fechaRegistro);

        when(ubicacionMascotaRepository.findTopByMascotaIdOrderByFechaRegistroDesc(888L)).thenReturn(Optional.of(ubicacion));

        UbicacionMascotaDTO resultado = ubicacionMascotaService.obtenerUbicacionPorMascota(888L);

        assertEquals(888L, resultado.getMascotaId());
        assertEquals(7.7, resultado.getLatitud());
        assertEquals(8.8, resultado.getLongitud());
        assertEquals(fechaRegistro, resultado.getFechaRegistro());
    }

    private UbicacionMascotaRequestDTO crearRequest(Long mascotaId, Double latitud, Double longitud, LocalDateTime fechaRegistro) {
        return UbicacionMascotaRequestDTO.builder()
                .mascotaId(mascotaId)
                .latitud(latitud)
                .longitud(longitud)
                .fechaRegistro(fechaRegistro)
                .build();
    }

    private UbicacionMascota crearEntidad(Long id, Long mascotaId, Double latitud, Double longitud, LocalDateTime fechaRegistro) {
        return UbicacionMascota.builder()
                .id(id)
                .mascotaId(mascotaId)
                .latitud(latitud)
                .longitud(longitud)
                .fechaRegistro(fechaRegistro)
                .build();
    }
}