package com.sanosysalvos.geolocalizacion.service.impl;

import com.sanosysalvos.geolocalizacion.dto.DistanciaResponseDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;
import com.sanosysalvos.geolocalizacion.model.UbicacionMascota;
import com.sanosysalvos.geolocalizacion.repository.UbicacionMascotaRepository;
import com.sanosysalvos.geolocalizacion.service.UbicacionMascotaService;
import com.sanosysalvos.geolocalizacion.util.GeospatialUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UbicacionMascotaServiceImpl implements UbicacionMascotaService {

    private final UbicacionMascotaRepository ubicacionMascotaRepository;

    @Override
    public UbicacionMascotaDTO guardarUbicacion(UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO) {
        UbicacionMascota ubicacionMascota = convertirAEntidad(ubicacionMascotaRequestDTO);
        UbicacionMascota ubicacionGuardada = ubicacionMascotaRepository.save(ubicacionMascota);
        return convertirADTO(ubicacionGuardada);
    }

    @Override
    public List<UbicacionMascotaDTO> listarUbicaciones() {
        return ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public UbicacionMascotaDTO obtenerUbicacionPorMascota(Long mascotaId) {
        UbicacionMascota ubicacionMascota = ubicacionMascotaRepository
                .findTopByMascotaIdOrderByFechaRegistroDesc(mascotaId)
                .orElseThrow(() -> new RuntimeException(
                        "Ubicacion no encontrada para la mascota con id: " + mascotaId
                ));

        return convertirADTO(ubicacionMascota);
    }

    @Override
    public DistanciaResponseDTO calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double distanciaKm = GeospatialUtils.calculateDistance(lat1, lon1, lat2, lon2);

        return DistanciaResponseDTO.builder()
                .distanciaKm(redondearADosDecimales(distanciaKm))
                .build();
    }

    @Override
    public List<UbicacionMascotaDTO> buscarUbicacionesCercanas(double lat, double lon, double radioKm) {
        return ubicacionMascotaRepository.findAllByOrderByFechaRegistroDesc()
                .stream()
                .filter(ubicacion -> GeospatialUtils.calculateDistance(
                        lat,
                        lon,
                        ubicacion.getLatitud(),
                        ubicacion.getLongitud()
                ) <= radioKm)
                .map(this::convertirADTO)
                .toList();
    }

    private UbicacionMascota convertirAEntidad(UbicacionMascotaRequestDTO ubicacionMascotaRequestDTO) {
        return UbicacionMascota.builder()
                .mascotaId(ubicacionMascotaRequestDTO.getMascotaId())
                .latitud(ubicacionMascotaRequestDTO.getLatitud())
                .longitud(ubicacionMascotaRequestDTO.getLongitud())
                .fechaRegistro(ubicacionMascotaRequestDTO.getFechaRegistro() != null
                        ? ubicacionMascotaRequestDTO.getFechaRegistro()
                        : LocalDateTime.now())
                .build();
    }

    private UbicacionMascotaDTO convertirADTO(UbicacionMascota ubicacionMascota) {
        return UbicacionMascotaDTO.builder()
                .id(ubicacionMascota.getId())
                .mascotaId(ubicacionMascota.getMascotaId())
                .latitud(ubicacionMascota.getLatitud())
                .longitud(ubicacionMascota.getLongitud())
                .fechaRegistro(ubicacionMascota.getFechaRegistro())
                .build();
    }

    private double redondearADosDecimales(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
