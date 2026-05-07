package com.sanosysalvos.mascotas.service.impl;

import com.sanosysalvos.mascotas.dto.MascotaDTO;
import com.sanosysalvos.mascotas.model.Mascota;
import com.sanosysalvos.mascotas.repository.MascotaRepository;
import com.sanosysalvos.mascotas.service.MascotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaServiceImpl implements MascotaService {

    private final MascotaRepository mascotaRepository;

    @Override
    public MascotaDTO crearMascota(MascotaDTO mascotaDTO) {
        Mascota mascota = convertirAEntidad(mascotaDTO);
        Mascota mascotaGuardada = mascotaRepository.save(mascota);
        return convertirADTO(mascotaGuardada);
    }

    @Override
    public List<MascotaDTO> listarMascotas() {
        return mascotaRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public MascotaDTO buscarMascotaPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));

        return convertirADTO(mascota);
    }

    @Override
    public List<MascotaDTO> listarPorEstado(String estado) {
        return mascotaRepository.findByEstado(estado)
                .stream()
                .map(this::convertirADTO)
                .toList();
    }

    @Override
    public MascotaDTO actualizarMascota(Long id, MascotaDTO mascotaDTO) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));

        mascota.setNombre(mascotaDTO.getNombre());
        mascota.setTipo(mascotaDTO.getTipo());
        mascota.setRaza(mascotaDTO.getRaza());
        mascota.setColor(mascotaDTO.getColor());
        mascota.setEstado(mascotaDTO.getEstado());

        Mascota mascotaActualizada = mascotaRepository.save(mascota);
        return convertirADTO(mascotaActualizada);
    }

    @Override
    public void eliminarMascota(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada con id: " + id));

        mascotaRepository.delete(mascota);
    }

    private Mascota convertirAEntidad(MascotaDTO mascotaDTO) {
        return Mascota.builder()
                .id(mascotaDTO.getId())
                .nombre(mascotaDTO.getNombre())
                .tipo(mascotaDTO.getTipo())
                .raza(mascotaDTO.getRaza())
                .color(mascotaDTO.getColor())
                .estado(mascotaDTO.getEstado())
                .build();
    }

    private MascotaDTO convertirADTO(Mascota mascota) {
        return MascotaDTO.builder()
                .id(mascota.getId())
                .nombre(mascota.getNombre())
                .tipo(mascota.getTipo())
                .raza(mascota.getRaza())
                .color(mascota.getColor())
                .estado(mascota.getEstado())
                .build();
    }
}