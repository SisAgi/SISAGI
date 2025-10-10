package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.model.Cliente;
import com.agibank.sisagi.model.Endereco;
import com.agibank.sisagi.model.Gerente;
import com.agibank.sisagi.model.Telefone;
import com.agibank.sisagi.model.enums.SegmentoCliente;
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
    private final ContaService contaService;
    private final ViaCepService viaCepService;

    @Transactional
    public ClienteResponse criar(ClienteRequest request) {
        if (clienteRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        if (clienteRepository.findByCpf(request.cpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        Cliente cliente = new Cliente();

        // Mapeamento dos campos obrigatórios da entidade Usuarios
        cliente.setNomeCompleto(request.nomeCompleto());
        cliente.setEmail(request.email());
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
        cliente.setRendaMensal(request.rendaMensal());
        cliente.setTempoEmprego(request.tempoEmprego());
        cliente.setPatrimonioEstimado(request.patrimonioEstimado());

        // Mapeamento dos objetos aninhados (Endereco e Telefone)
        Endereco endereco = new Endereco();

        // Inicia a lógica com os dados do ViaCEP
        ViaCepResponse viaCepResponse = viaCepService.buscarEnderecoPorCep(request.cep());

        endereco.setCep(request.cep());
        endereco.setLogradouro(viaCepResponse.logradouro());
        endereco.setCidade(viaCepResponse.localidade());
        endereco.setEstado(viaCepResponse.uf());
        endereco.setBairro(viaCepResponse.bairro());

        // Prioriza a entrada do usuário se ela existir
        if (request.logradouro() != null && !request.logradouro().isBlank()) {
            endereco.setLogradouro(request.logradouro());
        }
        if (request.cidade() != null && !request.cidade().isBlank()) {
            endereco.setCidade(request.cidade());
        }
        if (request.uf() != null && !request.uf().isBlank()) {
            endereco.setEstado(request.uf());
        }
        if (request.bairro() != null && !request.uf().isBlank()) {
            endereco.setBairro(request.bairro());
        }

        // Estes campos são fornecidos pelo usuário e não pelo ViaCEP
        endereco.setNumero(request.numero());
        endereco.setComplemento(request.complemento());
        endereco.setTipoEndereco(request.tipoEndereco());
        endereco.setCliente(cliente);
        cliente.getEnderecos().add(endereco);

        Telefone telefone = new Telefone();
        telefone.setDdi(request.ddi());
        telefone.setDdd(request.ddd());
        telefone.setTipoTelefone(request.tipoTelefone());
        telefone.setNumero(request.numeroTelefone());
        cliente.setTelefone(telefone);

        // Metodo para garantir que o cliente sempre tenha um id de gerente atribuido a criação da conta
        Gerente gerenteAssociado = gerenteRepository.findById(request.gerenteId())
                .orElseThrow(() -> new RecursoNaoEncontrado("Gerente não encontrado com o ID fornecido."));

        cliente.setGerente(gerenteAssociado);

        SegmentoCliente segmento = contaService.definirSegmentoCliente(
                request.rendaMensal(),
                request.patrimonioEstimado()
        );
        cliente.setSegmentoCliente(segmento);

        clienteRepository.save(cliente);
        return mapToClienteResponse(cliente);
    }

    //Busca um cliente específico usando o ID como referência
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("ID de cliente digitado não encontrado. ID: " + id));
        return mapToClienteResponse(cliente);
    }

    //Lista todos os clientes
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarTodos() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(this::mapToClienteResponse)
                .toList();
    }

    // Atualiza um cliente específico
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest request) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("ID de cliente não encontrado. ID: " + id));

        // Atualiza os campos de Cliente se não forem nulos
        if (request.nomeCompleto() != null) {
            clienteExistente.setNomeCompleto(request.nomeCompleto());
        }
        if (request.email() != null) {
            clienteExistente.setEmail(request.email());
        }
        if (request.rg() != null) {
            clienteExistente.setRg(request.rg());
        }
        if (request.estadoCivil() != null) {
            clienteExistente.setEstadoCivil(request.estadoCivil());
        }
        if (request.nomeSocial() != null) {
            clienteExistente.setNomeSocial(request.nomeSocial());
        }
        if (request.profissao() != null) {
            clienteExistente.setProfissao(request.profissao());
        }
        if (request.empresaAtual() != null) {
            clienteExistente.setEmpresaAtual(request.empresaAtual());
        }
        if (request.cargo() != null) {
            clienteExistente.setCargo(request.cargo());
        }
        if (request.rendaMensal() != null) {
            clienteExistente.setRendaMensal(request.rendaMensal());
        }
        if (request.tempoEmprego() != null) {
            clienteExistente.setTempoEmprego(request.tempoEmprego());
        }
        if (request.patrimonioEstimado() != null) {
            clienteExistente.setPatrimonioEstimado(request.patrimonioEstimado());
        }
        if (request.possuiRestricoesBancarias() != null) {
            clienteExistente.setPossuiRestricoesBancarias(request.possuiRestricoesBancarias());
        }
        if (request.ePpe() != null) {
            clienteExistente.setEPpe(request.ePpe());
        }

        // Atualiza os campos aninhados do Telefone
        if (clienteExistente.getTelefone() == null) {
            clienteExistente.setTelefone(new Telefone());
        }
        if (request.ddi() != null) {
            clienteExistente.getTelefone().setDdi(request.ddi());
        }
        if (request.ddd() != null) {
            clienteExistente.getTelefone().setDdd(request.ddd());
        }
        if (request.numeroTelefone() != null) {
            clienteExistente.getTelefone().setNumero(request.numeroTelefone());
        }
        if (request.tipoTelefone() != null) {
            clienteExistente.getTelefone().setTipoTelefone(request.tipoTelefone());
        }

        // Lógica para atualizar a lista de Endereços
        if (request.enderecos() != null) {
            // Limpa a lista existente de endereços para evitar duplicidade
            clienteExistente.getEnderecos().clear();
            for (EnderecoRequest enderecoRequest : request.enderecos()) {
                Endereco novoEndereco = new Endereco();
                novoEndereco.setCep(enderecoRequest.cep());
                novoEndereco.setLogradouro(enderecoRequest.logradouro());
                novoEndereco.setNumero(enderecoRequest.numero());
                novoEndereco.setComplemento(enderecoRequest.complemento());
                novoEndereco.setCidade(enderecoRequest.cidade()); // Atribui a cidade
                novoEndereco.setEstado(enderecoRequest.estado());
                novoEndereco.setTipoEndereco(enderecoRequest.tipoEndereco());
                novoEndereco.setCliente(clienteExistente);
                clienteExistente.getEnderecos().add(novoEndereco);
            }
        }
        Cliente clienteAtualizado = clienteRepository.save(clienteExistente);
        return mapToClienteResponse(clienteAtualizado);
    }

    //Deleta um cliente
    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontrado("ID de cliente não encontrado. ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    // Faz o trabalho de mapear os campos da entidade para o DTO de resposta
    private ClienteResponse mapToClienteResponse(Cliente cliente) {
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
                cliente.getRendaMensal(),
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
                                endereco.getBairro(),
                                endereco.getNumero(),
                                endereco.getComplemento(),
                                endereco.getCidade(),
                                endereco.getEstado(),
                                endereco.getTipoEndereco(),
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
