package com.sanosysalvos.geolocalizacion.service.impl;

import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaDTO;
import com.sanosysalvos.geolocalizacion.dto.UbicacionMascotaRequestDTO;
import com.sanosysalvos.geolocalizacion.model.UbicacionMascota;
import com.sanosysalvos.geolocalizacion.repository.UbicacionMascotaRepository;
import com.sanosysalvos.geolocalizacion.service.UbicacionMascotaService;
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
}
