package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.dto.MascotaDTO;
import com.sanosysalvos.bff.service.MascotasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MascotasBffServiceImpl implements MascotasBffService {

    private final RestTemplate restTemplate;

    @Value("${ms.mascotas.url}")
    private String mascotasUrl;

    @Override
    public MascotaDTO crearMascota(MascotaDTO mascotaDTO) {
        String url = mascotasUrl + "/api/mascotas";
        return restTemplate.postForObject(url, mascotaDTO, MascotaDTO.class);
    }

    @Override
    public Object listarMascotas() {
        String url = mascotasUrl + "/api/mascotas";
        return restTemplate.getForObject(url, Object.class);
    }

    @Override
    public Object buscarMascotaPorId(Long id) {
        String url = mascotasUrl + "/api/mascotas/" + id;
        return restTemplate.getForObject(url, Object.class);
    }

    @Override
    public Object listarPorEstado(String estado) {
        String url = mascotasUrl + "/api/mascotas/estado/" + estado;
        return restTemplate.getForObject(url, Object.class);
    }

    @Override
    public Object actualizarMascota(Long id, MascotaDTO mascotaDTO) {
        String url = mascotasUrl + "/api/mascotas/" + id;
        restTemplate.put(url, mascotaDTO);
        return "Mascota actualizada correctamente desde BFF";
    }

    @Override
    public void eliminarMascota(Long id) {
        String url = mascotasUrl + "/api/mascotas/" + id;
        restTemplate.delete(url);
    }
}