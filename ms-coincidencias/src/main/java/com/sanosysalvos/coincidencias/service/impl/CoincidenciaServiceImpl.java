package com.sanosysalvos.coincidencias.service.impl;

import com.sanosysalvos.coincidencias.client.MascotasFeignClient;
import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.dto.MascotaDTO;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CoincidenciaServiceImpl implements CoincidenciaService {

    private final MascotasFeignClient mascotasFeignClient;

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
                int porcentajeCoincidencia = calcularPorcentajeCoincidencia(perdida, encontrada);

                if (porcentajeCoincidencia >= 50) {
                    coincidencias.add(CoincidenciaDTO.builder()
                            .idMascotaPerdida(perdida.getId())
                            .idMascotaEncontrada(encontrada.getId())
                            .nombreMascotaPerdida(perdida.getNombre())
                            .nombreMascotaEncontrada(encontrada.getNombre())
                            .tipo(perdida.getTipo())
                            .raza(perdida.getRaza())
                            .color(perdida.getColor())
                            .edad(perdida.getEdad())
                            .dimension(perdida.getDimension())
                            .porcentajeCoincidencia(porcentajeCoincidencia)
                            .descripcion("Posible coincidencia por raza, color, edad y dimension")
                            .build());
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

        return coincidencias * 25;
    }

    private boolean coincidenIgnorandoMayusculas(String valorIzquierdo, String valorDerecho) {
        return valorIzquierdo != null
                && valorDerecho != null
                && valorIzquierdo.equalsIgnoreCase(valorDerecho);
    }
}