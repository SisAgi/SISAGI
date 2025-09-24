package com.agibank.sisagi.service;

import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuariosServices {
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;
}
