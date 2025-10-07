package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.DebitoAutomaticoRequest;
import com.agibank.sisagi.dto.DebitoAutomaticoResponse;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.DebitoAutomatico;
import com.agibank.sisagi.model.enums.StatusDebito;
import com.agibank.sisagi.repository.ContaRepository;
import com.agibank.sisagi.repository.DebitoAutomaticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebitoAutomaticoService {

    private final DebitoAutomaticoRepository debitoRepository;
    private final ContaRepository contaRepository;

    @Autowired
    public DebitoAutomaticoService(DebitoAutomaticoRepository debitoRepository, ContaRepository contaRepository) {
        this.debitoRepository = debitoRepository;
        this.contaRepository = contaRepository;
    }

    public DebitoAutomaticoResponse criarDebito(DebitoAutomaticoRequest request) {
        //Validação da Conta
        Conta conta = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada com ID: " + request.contaId()));

        //Validação de Duplicidade
        if (debitoRepository.findByIdentificadorConvenio(request.identificadorConvenio()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um débito automático cadastrado para este identificador de convênio.");
        }

        //Mapeamento e Persistência
        DebitoAutomatico debito = new DebitoAutomatico();
        debito.setConta(conta);
        debito.setDiaAgendado(request.diaAgendado());
        debito.setTipoServico(request.tipoServico());
        debito.setIdentificadorConvenio(request.identificadorConvenio());
        debito.setDescricao(request.descricao());
        debito.setStatus(StatusDebito.ATIVO);

        DebitoAutomatico salvo = debitoRepository.save(debito);
        return toResponse(salvo);
    }

    public DebitoAutomaticoResponse cancelarDebito(Long id) {
        DebitoAutomatico debito = debitoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regra de débito não encontrada com ID: " + id));

        // Regra de Negócio: Só podemos cancelar se estiver ATIVO ou SUSPENSO
        if (debito.getStatus() == StatusDebito.CANCELADO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O débito já está cancelado.");
        }

        debito.setStatus(StatusDebito.CANCELADO);
        DebitoAutomatico salvo = debitoRepository.save(debito);
        return toResponse(salvo);
    }

    public DebitoAutomaticoResponse buscarPorId(Long id) {
        DebitoAutomatico debito = debitoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regra de débito não encontrada com ID: " + id));
        return toResponse(debito);
    }

    public List<DebitoAutomaticoResponse> listarTodos() {
        return debitoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private DebitoAutomaticoResponse toResponse(DebitoAutomatico debito) {
        return new DebitoAutomaticoResponse(
                debito.getId(),
                debito.getConta().getId(),
                debito.getDiaAgendado(),
                debito.getTipoServico().getDescricao(),
                debito.getStatus().getDescricao(),
                debito.getIdentificadorConvenio(),
                debito.getDescricao()
        );
    }
}
