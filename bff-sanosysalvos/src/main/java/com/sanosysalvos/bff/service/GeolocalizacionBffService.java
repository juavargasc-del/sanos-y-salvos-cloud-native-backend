package com.sanosysalvos.bff.service;

import com.sanosysalvos.bff.dto.DistanciaResponseDTO;
import com.sanosysalvos.bff.dto.UbicacionMascotaDTO;

public interface GeolocalizacionBffService {

    UbicacionMascotaDTO guardarUbicacion(UbicacionMascotaDTO ubicacionMascotaDTO);

    Object listarUbicaciones();

    Object obtenerUbicacionPorMascota(Long mascotaId);

    DistanciaResponseDTO calcularDistancia(double lat1, double lon1, double lat2, double lon2);

    Object buscarUbicacionesCercanas(double lat, double lon, double radioKm);
}
