package com.sanosysalvos.coincidencias.service.impl;

import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;
import com.sanosysalvos.coincidencias.dto.MascotaDTO;
import com.sanosysalvos.coincidencias.service.CoincidenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CoincidenciaServiceImpl implements CoincidenciaService {

    private final RestTemplate restTemplate;

    @Value("${ms.mascotas.url}")
    private String mascotasUrl;

    @Override
    public List<CoincidenciaDTO> buscarCoincidencias() {
        String url = mascotasUrl + "/api/mascotas";

        MascotaDTO[] mascotasArray = restTemplate.getForObject(url, MascotaDTO[].class);

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
                if (esCoincidencia(perdida, encontrada)) {
                    coincidencias.add(CoincidenciaDTO.builder()
                            .idMascotaPerdida(perdida.getId())
                            .idMascotaEncontrada(encontrada.getId())
                            .nombreMascotaPerdida(perdida.getNombre())
                            .nombreMascotaEncontrada(encontrada.getNombre())
                            .tipo(perdida.getTipo())
                            .raza(perdida.getRaza())
                            .color(perdida.getColor())
                            .descripcion("Posible coincidencia por tipo, raza y color")
                            .build());
                }
            }
        }

        return coincidencias;
    }

    private boolean esCoincidencia(MascotaDTO perdida, MascotaDTO encontrada) {
        return perdida.getTipo() != null
                && perdida.getRaza() != null
                && perdida.getColor() != null
                && perdida.getTipo().equalsIgnoreCase(encontrada.getTipo())
                && perdida.getRaza().equalsIgnoreCase(encontrada.getRaza())
                && perdida.getColor().equalsIgnoreCase(encontrada.getColor());
    }
}