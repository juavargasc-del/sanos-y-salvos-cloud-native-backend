package com.sanosysalvos.geolocalizacion.service;

import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;

import java.util.List;

public interface UbicacionMascotaService {

    UbicacionMascotaDTO guardarUbicacion(UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO);

    List<UbicacionMascotaDTO> listarUbicaciones();

    UbicacionMascotaDTO obtenerUbicacionPorMascota(Long mascotaId);
}
