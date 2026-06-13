package com.sanosysalvos.coincidencias.service.impl;

import com.sanosysalvos.coincidencias.client.GeolocalizacionFeignClient;
import com.sanosysalvos.coincidencias.client.MascotasFeignClient;
import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.dto.DistanciaResponseDTO;
import com.sanosysalvos.coincidencias.dto.MascotaDTO;
import com.sanosysalvos.coincidencias.dto.UbicacionMascotaDTO;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CoincidenciaServiceImpl implements CoincidenciaService {

    private static final int PORCENTAJE_POR_CRITERIO = 25;
    private static final int PORCENTAJE_MAXIMO = 100;
    private static final double DISTANCIA_MAXIMA_KM = 5.0;
    private static final String DESCRIPCION_VISUAL =
            "Posible coincidencia por raza, color, edad y dimension";

    private final MascotasFeignClient mascotasFeignClient;
    private final GeolocalizacionFeignClient geolocalizacionFeignClient;

    @Override
    public List<CoincidenciaDTO> buscarCoincidencias() {
        MascotaDTO[] mascotasArray = mascotasFeignClient.listarMascotas();

        if (mascotasArray == null || mascotasArray.length == 0) {
            return new ArrayList<>();
        }

        List<MascotaDTO> mascotas = Arrays.asList(mascotasArray);
        List<CoincidenciaDTO> coincidencias = new ArrayList<>();

        List<MascotaDTO> perdidas = mascotas.stream()
                .filter(mascota -> "PERDIDA".equalsIgnoreCase(mascota.getEstado()))
                .toList();

        List<MascotaDTO> encontradas = mascotas.stream()
                .filter(mascota -> "ENCONTRADA".equalsIgnoreCase(mascota.getEstado()))
                .toList();

        for (MascotaDTO perdida : perdidas) {
            for (MascotaDTO encontrada : encontradas) {
                int porcentajeVisual = calcularPorcentajeCoincidencia(perdida, encontrada);

                if (porcentajeVisual >= 50) {
                    coincidencias.add(crearCoincidencia(perdida, encontrada, porcentajeVisual));
                }
            }
        }

        return coincidencias;
    }

    private int calcularPorcentajeCoincidencia(MascotaDTO perdida, MascotaDTO encontrada) {
        int coincidencias = 0;

        if (coincidenIgnorandoMayusculas(perdida.getRaza(), encontrada.getRaza())) {
            coincidencias++;
        }

        if (coincidenIgnorandoMayusculas(perdida.getColor(), encontrada.getColor())) {
            coincidencias++;
        }

        if (Objects.equals(perdida.getEdad(), encontrada.getEdad())) {
            coincidencias++;
        }

        if (coincidenIgnorandoMayusculas(perdida.getDimension(), encontrada.getDimension())) {
            coincidencias++;
        }

        return coincidencias * PORCENTAJE_POR_CRITERIO;
    }

    private boolean coincidenIgnorandoMayusculas(String valorIzquierdo, String valorDerecho) {
        return valorIzquierdo != null
                && valorDerecho != null
                && valorIzquierdo.equalsIgnoreCase(valorDerecho);
    }

    private CoincidenciaDTO crearCoincidencia(MascotaDTO perdida, MascotaDTO encontrada, int porcentajeVisual) {
        int porcentajeFinal = porcentajeVisual;
        String descripcion = DESCRIPCION_VISUAL;

        DistanciaResponseDTO distanciaResponseDTO = obtenerDistanciaEntreMascotas(perdida.getId(), encontrada.getId());

        if (distanciaResponseDTO != null
                && distanciaResponseDTO.getDistanciaKm() != null
                && distanciaResponseDTO.getDistanciaKm() <= DISTANCIA_MAXIMA_KM) {
            porcentajeFinal = Math.min(porcentajeVisual + PORCENTAJE_POR_CRITERIO, PORCENTAJE_MAXIMO);
            descripcion = descripcion + ". Coincidencia geografica valida. Distancia encontrada: "
                    + distanciaResponseDTO.getDistanciaKm() + " km";
        }

        return CoincidenciaDTO.builder()
                .idMascotaPerdida(perdida.getId())
                .idMascotaEncontrada(encontrada.getId())
                .nombreMascotaPerdida(perdida.getNombre())
                .nombreMascotaEncontrada(encontrada.getNombre())
                .tipo(perdida.getTipo())
                .raza(perdida.getRaza())
                .color(perdida.getColor())
                .edad(perdida.getEdad())
                .dimension(perdida.getDimension())
                .porcentajeCoincidencia(porcentajeFinal)
                .descripcion(descripcion)
                .build();
    }

    private DistanciaResponseDTO obtenerDistanciaEntreMascotas(Long mascotaPerdidaId, Long mascotaEncontradaId) {
        try {
            UbicacionMascotaDTO ubicacionPerdida = geolocalizacionFeignClient.obtenerUbicacionPorMascota(mascotaPerdidaId);
            UbicacionMascotaDTO ubicacionEncontrada = geolocalizacionFeignClient.obtenerUbicacionPorMascota(mascotaEncontradaId);

            if (ubicacionPerdida == null || ubicacionEncontrada == null) {
                return null;
            }

            return geolocalizacionFeignClient.calcularDistancia(
                    ubicacionPerdida.getLatitud(),
                    ubicacionPerdida.getLongitud(),
                    ubicacionEncontrada.getLatitud(),
                    ubicacionEncontrada.getLongitud()
            );
        } catch (FeignException exception) {
            return null;
        }
    }
}
