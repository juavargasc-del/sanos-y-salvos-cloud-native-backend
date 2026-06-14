package com.sanosysalvos.coincidencias.service;

import com.sanosysalvos.coincidencias.dto.CoincidenciaDTO;

import java.util.List;

public interface CoincidenciaService {

    List<CoincidenciaDTO> buscarCoincidencias();

    List<CoincidenciaDTO> buscarCoincidenciasPorUsuario(Long usuarioId);
}