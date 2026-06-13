package com.sanosysalvos.geolocalizacion.repository;

import com.sanosysalvos.geolocalizacion.model.UbicacionMascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionMascotaRepository extends JpaRepository<UbicacionMascota, Long> {

    List<UbicacionMascota> findAllByOrderByFechaRegistroDesc();

    Optional<UbicacionMascota> findTopByMascotaIdOrderByFechaRegistroDesc(Long mascotaId);
}
