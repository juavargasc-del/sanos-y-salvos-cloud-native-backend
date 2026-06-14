package com.sanosysalvos.mascotas.repository;

import com.sanosysalvos.mascotas.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    List<Mascota> findByEstado(String estado);

    List<Mascota> findByUsuarioId(Long usuarioId);

    List<Mascota> findByTipo(String tipo);

    List<Mascota> findByTipoAndColor(String tipo, String color);
}