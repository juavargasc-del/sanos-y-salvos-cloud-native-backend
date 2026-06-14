package com.sanosysalvos.bff.service;

public interface CoincidenciasBffService {

    Object buscarCoincidencias();

    Object buscarCoincidenciasPorUsuario(Long usuarioId);
}