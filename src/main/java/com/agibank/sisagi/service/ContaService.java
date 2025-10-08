package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.model.*;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.ContaRepository;
import jakarta.persistence.DiscriminatorValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public ContaCorrenteResponse criarContaCorrente(ContaCorrenteRequest request) {
        List<Cliente> titulares = clienteRepository.findAllById(request.titularIds());
        if (titulares.size() != request.titularIds().size()) {
            throw new IllegalArgumentException("Um ou mais IDs de clientes são inválidos");
        }
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumeroConta(request.numeroConta());
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setAgencia(request.agencia());
        contaCorrente.setLimiteChequeEspecial(request.limiteChequeEspecial());
        contaCorrente.setTitulares(new HashSet<>(titulares));
        contaRepository.save(contaCorrente);
        return maptoContaCorrenteResponse(contaCorrente);
    }

    @Transactional
    public ContaPoupResponse criarContaPoupanca(ContaPoupRequest request) {
        List<Cliente> titulares = clienteRepository.findAllById(request.titularIds());
        if (titulares.size() != request.titularIds().size()) {
            throw new IllegalArgumentException("Um ou mais IDs de clientes são inválidos");
        }
        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setNumeroConta(request.numeroConta());
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setAgencia(request.agencia());
        contaPoupanca.setRendimento(contaPoupanca.getRendimento());
        contaPoupanca.setTitulares(new HashSet<>(titulares));
        contaRepository.save(contaPoupanca);
        return maptoContaPoupancaResponse(contaPoupanca);
    }
    @Transactional
    public ContaJovemResponse criarContaJovem(ContaJovemRequest request){
        Conta responsavel = contaRepository.findById(request.responsavelId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente responsável não encontrada"));

        Set<Cliente> titulares = new HashSet<>(clienteRepository.findAllById(request.titularIds()));
        if (titulares.isEmpty()){
            throw new IllegalArgumentException("Nenhum cliente titular encontrado");
        }
        ContaJovem contaJovem = new ContaJovem();
        contaJovem.setNumeroConta(request.numeroConta());
        contaJovem.setSaldo(BigDecimal.ZERO);
        contaJovem.setAgencia(request.agencia());
        contaJovem.setResponsavelId(responsavel);
        contaJovem.setTitulares(titulares);
        contaRepository.save(contaJovem);
        return maptoContaJovemResponse(contaJovem);
    }

    @Transactional
    public void deletarConta(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new IllegalArgumentException("Conta não encontrada");
        }
        contaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public void buscarContaPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
    }

    @Transactional
    public ContaUpdateRequest atualizarConta(Long id, ContaUpdateRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Conta não encontrada"));
        conta.setAgencia(request.agencia());
        Conta contaAtualizada = contaRepository.save(conta);
        return new ContaUpdateRequest(contaAtualizada.getAgencia(), contaAtualizada.getNumeroConta(), request.cpf());
    }

    public ContaCorrenteResponse maptoContaCorrenteResponse(ContaCorrente conta) {
        Set<Long> titularIds = conta.getTitulares().stream()
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        return new ContaCorrenteResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getLimiteChequeEspecial(),
                getTipoConta(conta),
                titularIds);
    }
    public ContaPoupResponse maptoContaPoupancaResponse(ContaPoupanca conta) {
        Set<Long> titularIds = conta.getTitulares().stream()
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        return new ContaPoupResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getDataAniversario(),
                conta.getRendimento(),
                getTipoConta(conta),
                titularIds);
    }
    public ContaJovemResponse maptoContaJovemResponse(ContaJovem conta){
        Set<Long> titularIds = conta.getTitulares().stream()
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        return new ContaJovemResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getResponsavelId().getId(),
                getTipoConta(conta),
                titularIds);
    }
    public Object buscarDetalhesConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada com ID: " + contaId));
        if (conta instanceof ContaCorrente cc) {
            return maptoContaCorrenteResponse(cc);
        } else if (conta instanceof ContaPoupanca pp) {
            return maptoContaPoupancaResponse(pp);
        } else if (conta instanceof ContaJovem cj) {
            return maptoContaJovemResponse(cj);
        }
        throw new RecursoNaoEncontrado("Tipo de conta desconhecido para ID: " + contaId);
    }
    private static String getTipoConta(Conta conta){
        DiscriminatorValue discriminatorValue = conta.getClass().getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null){
            return discriminatorValue.value();
        } else {
            return "Tipo desconhecido";
        }
    }
}