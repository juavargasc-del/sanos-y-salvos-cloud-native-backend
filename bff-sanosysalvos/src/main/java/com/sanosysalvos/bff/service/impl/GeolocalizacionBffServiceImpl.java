package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.client.GeolocalizacionFeignClient;
import com.sanosysalvos.bff.dto.DistanciaResponseDTO;
import com.sanosysalvos.bff.dto.UbicacionMascotaDTO;
import com.sanosysalvos.bff.service.GeolocalizacionBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeolocalizacionBffServiceImpl implements GeolocalizacionBffService {

    private final GeolocalizacionFeignClient geolocalizacionFeignClient;

    @Override
    public UbicacionMascotaDTO guardarUbicacion(UbicacionMascotaDTO ubicacionMascotaDTO) {

        return geolocalizacionFeignClient.guardarUbicacion(ubicacionMascotaDTO);
    }

    @Override
    public Object listarUbicaciones() {

        return geolocalizacionFeignClient.listarUbicaciones();
    }

    @Override
    public Object obtenerUbicacionPorMascota(Long mascotaId) {

        return geolocalizacionFeignClient.obtenerUbicacionPorMascota(mascotaId);
    }

    @Override
    public DistanciaResponseDTO calcularDistancia(double lat1, double lon1, double lat2, double lon2) {

        return geolocalizacionFeignClient.calcularDistancia(lat1, lon1, lat2, lon2);
    }

    @Override
    public Object buscarUbicacionesCercanas(double lat, double lon, double radioKm) {

        return geolocalizacionFeignClient.buscarUbicacionesCercanas(lat, lon, radioKm);
    }
}
