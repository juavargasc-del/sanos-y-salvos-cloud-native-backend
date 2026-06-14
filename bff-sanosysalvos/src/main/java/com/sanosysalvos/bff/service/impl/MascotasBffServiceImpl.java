package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.client.MascotasFeignClient;
import com.sanosysalvos.bff.dto.MascotaDTO;
import com.sanosysalvos.bff.service.MascotasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MascotasBffServiceImpl implements MascotasBffService {

    private final MascotasFeignClient mascotasFeignClient;

    @Override
    public MascotaDTO crearMascota(MascotaDTO mascotaDTO) {

        return mascotasFeignClient.crearMascota(mascotaDTO);
    }

    @Override
    public Object listarMascotas() {

        return mascotasFeignClient.listarMascotas();
    }

    @Override
    public Object buscarMascotaPorId(Long id) {

        return mascotasFeignClient.buscarMascotaPorId(id);
    }

    @Override
    public Object listarPorEstado(String estado) {

        return mascotasFeignClient.listarPorEstado(estado);
    }

    @Override
    public Object listarPorUsuarioId(Long usuarioId) {

        return mascotasFeignClient.listarPorUsuarioId(usuarioId);
    }

    @Override
    public Object actualizarMascota(Long id, MascotaDTO mascotaDTO) {

        mascotasFeignClient.actualizarMascota(id, mascotaDTO);
        return "Mascota actualizada correctamente desde BFF";
    }

    @Override
    public void eliminarMascota(Long id) {

        mascotasFeignClient.eliminarMascota(id);
    }
}