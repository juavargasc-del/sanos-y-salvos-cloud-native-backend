package com.sanosysalvos.mascotas.service;

import com.sanosysalvos.mascotas.dto.MascotaDTO;

import java.util.List;

public interface MascotaService {

    MascotaDTO crearMascota(MascotaDTO mascotaDTO);

    List<MascotaDTO> listarMascotas();

    MascotaDTO buscarMascotaPorId(Long id);

    List<MascotaDTO> listarPorEstado(String estado);

    List<MascotaDTO> listarPorUsuarioId(Long usuarioId);

    MascotaDTO actualizarMascota(Long id, MascotaDTO mascotaDTO);

    void eliminarMascota(Long id);
}