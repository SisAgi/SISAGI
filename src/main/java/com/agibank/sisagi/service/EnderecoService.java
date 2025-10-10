package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.EnderecoRequest;
import com.agibank.sisagi.dto.EnderecoResponse;
import com.agibank.sisagi.dto.EnderecoUpdateRequest;
import com.agibank.sisagi.dto.ViaCepResponse;
import com.agibank.sisagi.model.Cliente;
import com.agibank.sisagi.model.Endereco;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.EnderecoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnderecoService {
    private final EnderecoRepository enderecoRepository;
    private final ClienteRepository clienteRepository;
    private final ViaCepService viaCepService;

    // Adiciona endereços a um cadastro
    @Transactional
    public EnderecoResponse criar(EnderecoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));

        // Busca dados do endereço via ViaCEP
        ViaCepResponse viaCepData = viaCepService.buscarEnderecoPorCep(request.cep());

        Endereco endereco = new Endereco();
        endereco.setCep(viaCepData.cep());
        endereco.setLogradouro(viaCepData.logradouro());
        endereco.setBairro(viaCepData.bairro());
        endereco.setNumero(request.numero()); // Usuário deve informar
        endereco.setComplemento(request.complemento()); // Usuário deve informar
        endereco.setCidade(viaCepData.localidade());
        endereco.setEstado(viaCepData.uf());
        endereco.setTipoEndereco(request.tipoEndereco());
        endereco.setCliente(cliente);

        enderecoRepository.save(endereco);
        return mapToEnderecoResponse(endereco);
    }

    // Consulta endereços por "ID"
    @Transactional(readOnly = true)
    public EnderecoResponse buscarPorId(Long id) {

        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        return mapToEnderecoResponse(endereco);
    }

    // Consulta todos os endereços
    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarTodos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
                .map(this::mapToEnderecoResponse)
                .toList();
    }

    // Consulta endereço por CEP
    @Transactional(readOnly = true)
    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        return viaCepService.buscarEnderecoPorCep(cep);
    }

    // Lista todos os endereços de um cliente
    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarPorCliente(Long clienteId) {
        List<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId);
        return enderecos.stream()
                .map(this::mapToEnderecoResponse)
                .toList();
    }

    // Busca endereço por tipo
    @Transactional(readOnly = true)
    public EnderecoResponse buscarPorClienteETipo(Long clienteId, String tipoEndereco) {
        Endereco endereco = enderecoRepository.findByClienteIdAndTipoEndereco(clienteId, tipoEndereco)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado para o cliente e tipo especificados"));
        return mapToEnderecoResponse(endereco);
    }

    // Atualiza endereços
    @Transactional
    public EnderecoResponse atualizar(Long id, EnderecoUpdateRequest request) {
        Endereco enderecoExistente = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));

        enderecoExistente.setCep(request.cep());
        enderecoExistente.setLogradouro(request.logradouro());
        enderecoExistente.setNumero(request.numero());
        enderecoExistente.setComplemento(request.complemento());
        enderecoExistente.setCidade(request.cidade());
        enderecoExistente.setEstado(request.estado());
        enderecoExistente.setTipoEndereco(request.tipoEndereco());

        Endereco enderecoAtualizado = enderecoRepository.save(enderecoExistente);
        return mapToEnderecoResponse(enderecoAtualizado);
    }

    // Deleta endereços
    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new IllegalArgumentException("Endereço não encontrado");
        }
        enderecoRepository.deleteById(id);
    }

    // Tem a função de mapear os campos da entidade para o seu respectivo DTO de resposta
    private EnderecoResponse mapToEnderecoResponse(Endereco endereco) {
        return new EnderecoResponse(
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
        );
    }
}
