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

    @Transactional
    public EnderecoResponse criar(EnderecoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        
        // Busca dados do endereço via ViaCEP
        ViaCepResponse viaCepData = viaCepService.buscarEnderecoPorCep(request.cep());
        
        Endereco endereco = new Endereco();
        endereco.setCep(viaCepData.cep());
        endereco.setLogradouro(viaCepData.logradouro());
        endereco.setNumero(request.numero()); // Usuário deve informar
        endereco.setComplemento(request.complemento()); // Usuário deve informar
        endereco.setCidade(viaCepData.localidade());
        endereco.setEstado(viaCepData.uf());
        endereco.setTipoEndereco(request.tipoEndereco());
        endereco.setCliente(cliente);
        
        enderecoRepository.save(endereco);
        return mapToEnderecoResponse(endereco);
    }

    @Transactional(readOnly = true)
    public EnderecoResponse buscarPorId(Long id) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado"));
        return mapToEnderecoResponse(endereco);
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarTodos() {
        List<Endereco> enderecos = enderecoRepository.findAll();
        return enderecos.stream()
                .map(this::mapToEnderecoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ViaCepResponse buscarEnderecoPorCep(String cep) {
        return viaCepService.buscarEnderecoPorCep(cep);
    }

    @Transactional(readOnly = true)
    public List<EnderecoResponse> listarPorCliente(Long clienteId) {
        List<Endereco> enderecos = enderecoRepository.findByClienteId(clienteId);
        return enderecos.stream()
                .map(this::mapToEnderecoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnderecoResponse buscarPorClienteETipo(Long clienteId, String tipoEndereco) {
        Endereco endereco = enderecoRepository.findByClienteIdAndTipoEndereco(clienteId, tipoEndereco)
                .orElseThrow(() -> new IllegalArgumentException("Endereço não encontrado para o cliente e tipo especificados"));
        return mapToEnderecoResponse(endereco);
    }

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

    @Transactional
    public void deletar(Long id) {
        if (!enderecoRepository.existsById(id)) {
            throw new IllegalArgumentException("Endereço não encontrado");
        }
        enderecoRepository.deleteById(id);
    }

    private EnderecoResponse mapToEnderecoResponse(Endereco endereco) {
        return new EnderecoResponse(
                endereco.getIdEndereco(),
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getTipoEndereco(),
                endereco.getCliente().getId()
        );
    }
}
