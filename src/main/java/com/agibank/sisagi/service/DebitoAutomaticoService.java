package com.agibank.sisagi.service;

import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.DebitoAutomatico;
import com.agibank.sisagi.model.enums.StatusDebito;
import com.agibank.sisagi.repository.ContaRepository;
import com.agibank.sisagi.repository.DebitoAutomaticoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DebitoAutomaticoService {

    private final DebitoAutomaticoRepository debitoAutomaticoRepository;
    private final ContaRepository contaRepository;

    @Autowired
    public DebitoAutomaticoService(DebitoAutomaticoRepository debitoAutomaticoRepository, ContaRepository contaRepository) {
        this.debitoAutomaticoRepository = debitoAutomaticoRepository;
        this.contaRepository = contaRepository;
    }

    public DebitoAutomatico salvarNovoDebito(DebitoAutomatico debito) {
        if (debito.getNumeroReferencia() == null || debito.getNumeroReferencia().trim().isEmpty()) {
            throw new IllegalArgumentException("O número de referência é obrigatório.");
        }

        if (debitoAutomaticoRepository.findByNumeroReferencia(debito.getNumeroReferencia()) != null) {
            throw new IllegalStateException("Já existe um débito agendado com este número de referência.");
        }

        Conta conta = contaRepository.findById(debito.getConta().getId())
                .orElseThrow(() -> new NoSuchElementException("Conta bancária não encontrada."));

        debito.setStatus(StatusDebito.PENDENTE);

        return debitoAutomaticoRepository.save(debito);
    }

    public DebitoAutomatico buscarPorNumeroReferencia(String numeroReferencia) {
        return debitoAutomaticoRepository.findByNumeroReferencia(numeroReferencia);
    }
}
