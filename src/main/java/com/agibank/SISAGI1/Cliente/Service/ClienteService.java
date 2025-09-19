package com.agibank.SISAGI1.Cliente.Service;

import com.agibank.SISAGI1.Cliente.DTOs.ClienteRequest;
import com.agibank.SISAGI1.Cliente.DTOs.ClienteResponse;
import com.agibank.SISAGI1.Cliente.DTOs.ClienteUpdateRequest;
import com.agibank.SISAGI1.Cliente.Entity.Cliente;
import com.agibank.SISAGI1.Gerente.Entity.Gerente;
import com.agibank.SISAGI1.Conta.Entity.UserRole;
import com.agibank.SISAGI1.Cliente.Repository.ClienteRepository;
import com.agibank.SISAGI1.Gerente.Repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        if (clienteRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setSenha(request.senha());
        cliente.setRole(UserRole.CLIENTE);

        if (request.gerenteId() != null) {
            Gerente gerenteAssociado = gerenteRepository.findById(request.gerenteId())
                    .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado"));
            cliente.setGerente(gerenteAssociado);;
        }
        clienteRepository.save(cliente);
        return mapToClienteResponse(cliente);
    }
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return mapToClienteResponse(cliente);
    }
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::mapToClienteResponse)
                .toList();
    }
    @Transactional(readOnly = true)
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest request) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        clienteExistente.setNome(request.nome());
        clienteExistente.setEmail(request.email());
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return mapToClienteResponse(clienteAtualizado);
    }
@Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }
    private ClienteResponse mapToClienteResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getCpf());
    }

}
