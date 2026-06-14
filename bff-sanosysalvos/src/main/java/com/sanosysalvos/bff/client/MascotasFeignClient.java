package com.sanosysalvos.bff.client;

import com.sanosysalvos.bff.dto.MascotaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "mascotasFeignClient", url = "${ms.mascotas.url}")
public interface MascotasFeignClient {

    @PostMapping("/api/mascotas")
    MascotaDTO crearMascota(@RequestBody MascotaDTO mascotaDTO);

    @GetMapping("/api/mascotas")
    Object listarMascotas();

    @GetMapping("/api/mascotas/{id}")
    Object buscarMascotaPorId(@PathVariable("id") Long id);

    @GetMapping("/api/mascotas/estado/{estado}")
    Object listarPorEstado(@PathVariable("estado") String estado);

    @GetMapping("/api/mascotas/usuario/{usuarioId}")
    Object listarPorUsuarioId(@PathVariable("usuarioId") Long usuarioId);

    @PutMapping("/api/mascotas/{id}")
    void actualizarMascota(@PathVariable("id") Long id, @RequestBody MascotaDTO mascotaDTO);

    @DeleteMapping("/api/mascotas/{id}")
    void eliminarMascota(@PathVariable("id") Long id);
}