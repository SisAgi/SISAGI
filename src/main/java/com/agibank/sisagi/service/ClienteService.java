package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.ClienteRequest;
import com.agibank.sisagi.dto.ClienteResponse;
import com.agibank.sisagi.dto.ClienteUpdateRequest;
import com.agibank.sisagi.model.Cliente;
import com.agibank.sisagi.model.Endereco;
import com.agibank.sisagi.model.Gerente;
import com.agibank.sisagi.model.Telefone;
import com.agibank.sisagi.model.enums.UserRole;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.GerenteRepository;
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
        cliente.setCpf(request.cpf());
        cliente.setRole(UserRole.CLIENTE);

        Endereco endereco = new Endereco();
        endereco.setCep(request.cep());
        endereco.setLogradouro(request.logradouro());
        endereco.setComplemento(request.complemento());
        endereco.setCidade(request.cidade());
        endereco.setEstado(request.uf());
        endereco.setTipoEndereco(request.tipoEndereco());
        endereco.setNumero(request.numero());
        endereco.setCliente(cliente);
        cliente.getEnderecos().add(endereco);

        Telefone telefone = new Telefone();
        telefone.setDdi(request.ddi());
        telefone.setDdd(request.ddd());
        telefone.setTipoTelefone(request.tipoTelefone());
        telefone.setNumero(request.numeroTelefone());
        cliente.setTelefone(telefone);


        if (request.gerenteId() != null) {
            Gerente gerenteAssociado = gerenteRepository.findById(request.gerenteId())
                    .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado"));
            cliente.setGerente(gerenteAssociado);
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

    @Transactional
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
