package com.sanosysalvos.geolocalizacion.service;

import com.sanosysalvos.geolocalizacion.dto.DistanciaResponseDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;

import java.util.List;

public interface UbicacionMascotaService {

    UbicacionMascotaDTO guardarUbicacion(UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO);

    List<UbicacionMascotaDTO> listarUbicaciones();

    UbicacionMascotaDTO obtenerUbicacionPorMascota(Long mascotaId);

    DistanciaResponseDTO calcularDistancia(double lat1, double lon1, double lat2, double lon2);

    List<UbicacionMascotaDTO> buscarUbicacionesCercanas(double lat, double lon, double radioKm);
}
