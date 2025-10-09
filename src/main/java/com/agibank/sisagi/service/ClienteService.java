package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
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

        // Mapeamento dos campos obrigatórios da entidade Usuarios
        cliente.setNomeCompleto(request.nomeCompleto());
        cliente.setEmail(request.email());
        cliente.setSenha(request.senha());
        cliente.setCpf(request.cpf());
        cliente.setRole(UserRole.CLIENTE);

        // Mapeamento dos novos campos obrigatórios
        cliente.setDataNascimento(request.dataNascimento());
        cliente.setDataEmissaoDocumento(request.dataEmissaoDocumento());
        cliente.setRg(request.rg());
        cliente.setNomePai(request.nomePai());
        cliente.setNomeMae(request.nomeMae());
        cliente.setEstadoCivil(request.estadoCivil());
        cliente.setPossuiRestricoesBancarias(request.possuiRestricoesBancarias());
        cliente.setEPpe(request.ePpe());

        // Mapeamento dos campos opcionais
        cliente.setCargo(request.cargo());
        cliente.setEmpresaAtual(request.empresaAtual());
        cliente.setProfissao(request.profissao());
        cliente.setSalarioMensal(request.salarioMensal());
        cliente.setTempoEmprego(request.tempoEmprego());
        cliente.setPatrimonioEstimado(request.patrimonioEstimado());

        // Mapeamento dos objetos aninhados (Endereco e Telefone)
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
                    .orElseThrow(() -> new RecursoNaoEncontrado("Gerente não encontrado. ID: "+ request.gerenteId()));
            cliente.setGerente(gerenteAssociado);
        }

        clienteRepository.save(cliente);
        return mapToClienteResponse(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("ID de cliente digitado não encontrado. ID: "+id));
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
                .orElseThrow(() -> new RecursoNaoEncontrado("ID de cliente não encontrado. ID: "+id));
        clienteExistente.setNomeCompleto(request.nome());
        clienteExistente.setEmail(request.email());
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return mapToClienteResponse(clienteAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontrado("ID de cliente não encontrado. ID: "+id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponse mapToClienteResponse(Cliente cliente) {
        // Mapear os campos da entidade para o DTO de resposta
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNomeCompleto(),
                cliente.getEmail(),
                cliente.getCpf(),
                cliente.getDataNascimento(),
                cliente.getRg(),
                cliente.getDataEmissaoDocumento(),
                cliente.getNomePai(),
                cliente.getNomeMae(),
                cliente.getEstadoCivil(),
                cliente.getNomeSocial(),
                cliente.getProfissao(),
                cliente.getEmpresaAtual(),
                cliente.getCargo(),
                cliente.getSalarioMensal(),
                cliente.getTempoEmprego(),
                cliente.getPatrimonioEstimado(),
                cliente.getPossuiRestricoesBancarias(),
                cliente.getEPpe(),
                cliente.getRole(),
                cliente.getEnderecos().stream()
                        .map(endereco -> new EnderecoResponse(
                                endereco.getIdEndereco(),
                                endereco.getCep(),
                                endereco.getLogradouro(),
                                endereco.getComplemento(),
                                endereco.getCidade(),
                                endereco.getEstado(),
                                endereco.getTipoEndereco(),
                                endereco.getNumero(),
                                endereco.getCliente().getId()
                        ))
                        .toList(),
                new TelefoneResponse(
                        cliente.getTelefone().getDdi(),
                        cliente.getTelefone().getDdd(),
                        cliente.getTelefone().getNumero(),
                        cliente.getTelefone().getTipoTelefone()
                )
        );
    }

}
