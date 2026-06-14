package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.dto.MascotaDTO;

public interface MascotasBffService {

    MascotaDTO crearMascota(MascotaDTO mascotaDTO);

    Object listarMascotas();

    Object buscarMascotaPorId(Long id);

    Object listarPorEstado(String estado);

    Object listarPorUsuarioId(Long usuarioId);

    Object actualizarMascota(Long id, MascotaDTO mascotaDTO);

    void eliminarMascota(Long id);
}