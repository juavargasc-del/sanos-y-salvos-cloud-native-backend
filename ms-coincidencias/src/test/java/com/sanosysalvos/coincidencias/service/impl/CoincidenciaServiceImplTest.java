package com.sanosysalvos.coincidencias.service.impl;

import com.sanosysalvos.coincidencias.client.GeolocalizacionFeignClient;
import com.sanosysalvos.coincidencias.client.MascotasFeignClient;
import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.dto.DistanciaResponseDTO;
import com.sanosysalvos.coincidencias.dto.MascotaDTO;
import com.sanosysalvos.coincidencias.dto.UbicacionMascotaDTO;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoincidenciaServiceImplTest {

    @Mock
    private MascotasFeignClient mascotasFeignClient;

    @Mock
    private GeolocalizacionFeignClient geolocalizacionFeignClient;

    @InjectMocks
    private CoincidenciaServiceImpl coincidenciaService;

    @Test
    void buscarCoincidencias_debeRetornarListaVaciaCuandoListadoEsNull() {
        when(mascotasFeignClient.listarMascotas()).thenReturn(null);

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertTrue(resultado.isEmpty());
        verify(mascotasFeignClient).listarMascotas();
        verifyNoInteractions(geolocalizacionFeignClient);
    }

    @Test
    void buscarCoincidencias_debeRetornarListaVaciaCuandoListadoEsVacio() {
        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[0]);

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertTrue(resultado.isEmpty());
        verify(mascotasFeignClient).listarMascotas();
        verifyNoInteractions(geolocalizacionFeignClient);
    }

    @Test
    void buscarCoincidencias_debeGenerarCoincidenciaCuandoRazaYColorCoinciden() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 8, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(50, resultado.get(0).getPorcentajeCoincidencia());
        assertEquals(1L, resultado.get(0).getIdMascotaPerdida());
        assertEquals(2L, resultado.get(0).getIdMascotaEncontrada());
        verify(mascotasFeignClient).listarMascotas();
    }

    @Test
    void buscarCoincidencias_debeGenerarCoincidenciaCuandoCuatroAtributosCoinciden() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Grande", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getPorcentajeCoincidencia());
        verify(mascotasFeignClient).listarMascotas();
    }

    @Test
    void buscarCoincidencias_noDebeGenerarCoincidenciaCuandoPorcentajeEsMenorA50() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Gato", "Siames", "Negro", 8, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertTrue(resultado.isEmpty());
        verify(mascotasFeignClient).listarMascotas();
        verifyNoInteractions(geolocalizacionFeignClient);
    }

    @Test
    void buscarCoincidencias_ignoraMascotasDelMismoEstado() {
        MascotaDTO perdidaUno = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO perdidaDos = crearMascota(2L, "Milo", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdidaUno, perdidaDos});

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertTrue(resultado.isEmpty());
        verify(mascotasFeignClient).listarMascotas();
        verifyNoInteractions(geolocalizacionFeignClient);
    }

    @Test
    void buscarCoincidenciasPorUsuario_retornaVacioCuandoElUsuarioNoTieneMascotas() {
        when(mascotasFeignClient.listarMascotasPorUsuario(10L)).thenReturn(new MascotaDTO[0]);

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidenciasPorUsuario(10L);

        assertTrue(resultado.isEmpty());
        verify(mascotasFeignClient).listarMascotasPorUsuario(10L);
        verify(mascotasFeignClient, never()).listarMascotas();
        verifyNoInteractions(geolocalizacionFeignClient);
    }

    @Test
    void buscarCoincidenciasPorUsuario_filtraCorrectamenteCoincidenciasDelUsuario() {
        MascotaDTO mascotaUsuario = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO mascotaEncontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Grande", "ENCONTRADA");
        MascotaDTO mascotaAjenaPerdida = crearMascota(3L, "Rex", "Gato", "Siames", "Blanco", 4, "Pequena", "PERDIDA");
        MascotaDTO mascotaAjenaEncontrada = crearMascota(4L, "Mia", "Gato", "Siames", "Blanco", 4, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotasPorUsuario(10L)).thenReturn(new MascotaDTO[]{mascotaUsuario});
        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{mascotaUsuario, mascotaEncontrada, mascotaAjenaPerdida, mascotaAjenaEncontrada});

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidenciasPorUsuario(10L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdMascotaPerdida());
        assertEquals(2L, resultado.get(0).getIdMascotaEncontrada());
        verify(mascotasFeignClient).listarMascotasPorUsuario(10L);
        verify(mascotasFeignClient).listarMascotas();
    }

    @Test
    void buscarCoincidencias_debeAgregarCoincidenciaGeograficaCuandoDistanciaEsMenorOIgualA5Km() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(crearUbicacion(1L, 10.0, 20.0));
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(2L)).thenReturn(crearUbicacion(2L, 10.1, 20.1));
        when(geolocalizacionFeignClient.calcularDistancia(10.0, 20.0, 10.1, 20.1))
                .thenReturn(DistanciaResponseDTO.builder().distanciaKm(4.5).build());

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getPorcentajeCoincidencia());
        assertTrue(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
        verify(geolocalizacionFeignClient).obtenerUbicacionPorMascota(1L);
        verify(geolocalizacionFeignClient).obtenerUbicacionPorMascota(2L);
        verify(geolocalizacionFeignClient).calcularDistancia(10.0, 20.0, 10.1, 20.1);
    }

    @Test
    void buscarCoincidencias_debeMantenerPorcentajeSinAumentoCuandoDistanciaEsMayorA5Km() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(crearUbicacion(1L, 10.0, 20.0));
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(2L)).thenReturn(crearUbicacion(2L, 11.0, 21.0));
        when(geolocalizacionFeignClient.calcularDistancia(10.0, 20.0, 11.0, 21.0))
                .thenReturn(DistanciaResponseDTO.builder().distanciaKm(6.0).build());

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(75, resultado.get(0).getPorcentajeCoincidencia());
        assertFalse(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
    }

    @Test
    void buscarCoincidencias_debeMantenerPorcentajeCuandoUbicacionPorMascotaEsNull() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(null);

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(75, resultado.get(0).getPorcentajeCoincidencia());
        assertFalse(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
        verify(geolocalizacionFeignClient).obtenerUbicacionPorMascota(1L);
        verify(geolocalizacionFeignClient, never()).calcularDistancia(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    void buscarCoincidencias_debeMantenerPorcentajeCuandoCalcularDistanciaDevuelveNull() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(crearUbicacion(1L, 10.0, 20.0));
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(2L)).thenReturn(crearUbicacion(2L, 10.1, 20.1));
        when(geolocalizacionFeignClient.calcularDistancia(10.0, 20.0, 10.1, 20.1)).thenReturn(null);

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(75, resultado.get(0).getPorcentajeCoincidencia());
        assertFalse(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
    }

    @Test
    void buscarCoincidencias_debeRetornarCoincidenciaVisualCuandoFeignExceptionEsLanzada() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Pequena", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenThrow(crearFeignException());

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(75, resultado.get(0).getPorcentajeCoincidencia());
        assertFalse(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
    }

    @Test
    void buscarCoincidencias_debeIncluirDescripcionGeograficaValidaCuandoCorresponde() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Grande", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(crearUbicacion(1L, 10.0, 20.0));
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(2L)).thenReturn(crearUbicacion(2L, 10.1, 20.1));
        when(geolocalizacionFeignClient.calcularDistancia(10.0, 20.0, 10.1, 20.1))
                .thenReturn(DistanciaResponseDTO.builder().distanciaKm(3.2).build());

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
    }

    @Test
    void buscarCoincidencias_debeLimitarPorcentajeMaximoA100() {
        MascotaDTO perdida = crearMascota(1L, "Luna", "Perro", "Labrador", "Negro", 3, "Grande", "PERDIDA");
        MascotaDTO encontrada = crearMascota(2L, "Max", "Perro", "Labrador", "Negro", 3, "Grande", "ENCONTRADA");

        when(mascotasFeignClient.listarMascotas()).thenReturn(new MascotaDTO[]{perdida, encontrada});
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(1L)).thenReturn(crearUbicacion(1L, 10.0, 20.0));
        when(geolocalizacionFeignClient.obtenerUbicacionPorMascota(2L)).thenReturn(crearUbicacion(2L, 10.1, 20.1));
        when(geolocalizacionFeignClient.calcularDistancia(10.0, 20.0, 10.1, 20.1))
                .thenReturn(DistanciaResponseDTO.builder().distanciaKm(2.0).build());

        List<CoincidenciaDTO> resultado = coincidenciaService.buscarCoincidencias();

        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getPorcentajeCoincidencia());
        assertTrue(resultado.get(0).getDescripcion().contains("Coincidencia geografica valida"));
    }

    private MascotaDTO crearMascota(Long id, String nombre, String tipo, String raza, String color, Integer edad, String dimension, String estado) {
        return MascotaDTO.builder()
                .id(id)
                .nombre(nombre)
                .tipo(tipo)
                .raza(raza)
                .color(color)
                .edad(edad)
                .dimension(dimension)
                .estado(estado)
                .build();
    }

    private UbicacionMascotaDTO crearUbicacion(Long mascotaId, Double latitud, Double longitud) {
        return UbicacionMascotaDTO.builder()
                .id(mascotaId)
                .mascotaId(mascotaId)
                .latitud(latitud)
                .longitud(longitud)
                .fechaRegistro(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();
    }

    private FeignException crearFeignException() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://localhost/api/geolocalizacion/mascota/1",
                Collections.<String, Collection<String>>emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );

        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.<String, Collection<String>>emptyMap())
                .build();

        return FeignException.errorStatus("geolocalizacionFeignClient", response);
    }
}