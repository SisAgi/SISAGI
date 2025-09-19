package com.agibank.SISAGI1.Conta.Service;

import com.agibank.SISAGI1.Cliente.Repository.ClienteRepository;
import com.agibank.SISAGI1.Gerente.Repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuariosServices{
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;


}
