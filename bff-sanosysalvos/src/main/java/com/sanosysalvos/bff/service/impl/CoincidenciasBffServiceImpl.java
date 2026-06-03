package com.sanosysalvos.bff.service.impl;

import com.sanosysalvos.bff.client.CoincidenciasFeignClient;
import com.sanosysalvos.bff.service.CoincidenciasBffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoincidenciasBffServiceImpl implements CoincidenciasBffService {

    private final CoincidenciasFeignClient coincidenciasFeignClient;

    @Override
    public Object buscarCoincidencias() {

        return coincidenciasFeignClient.buscarCoincidencias();
    }
}