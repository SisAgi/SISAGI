package com.agibank.SISAGI1.Conta.Service;

import com.agibank.SISAGI1.Repositories.ClienteRepository;
import com.agibank.SISAGI1.Repositories.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuariosServices{
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;


}
