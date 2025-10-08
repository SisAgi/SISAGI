package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.exception.SaldoInsuficienteException;
import com.agibank.sisagi.exception.SaldoInvalido;
import com.agibank.sisagi.model.*;
import com.agibank.sisagi.model.enums.StatusConta;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.ContaRepository;
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
    public ContaCorrenteResponse CriarContaCorrente(ContaCorrenteRequest request) {
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
        contaCorrente.setStatusConta(StatusConta.ATIVA);
        contaRepository.save(contaCorrente);
        return maptoContaCorrenteResponse(contaCorrente);
    }

    @Transactional
    public ContaPoupResponse CriarContaPoupanca(ContaPoupRequest request) {
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
        contaPoupanca.setStatusConta(StatusConta.ATIVA);
        contaRepository.save(contaPoupanca);
        return maptoContaPoupancaResponse(contaPoupanca);
    }
    @Transactional
    public ContaJovemResponse CriarContaJovem(ContaJovemRequest request){
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
        contaJovem.setStatusConta(StatusConta.ATIVA);
        contaRepository.save(contaJovem);
        return maptoContaJovemResponse(contaJovem);
    }

    @Transactional
    public void desativarConta(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(()-> new RecursoNaoEncontrado("Conta não encontrada"));
        if (conta.getSaldo() != BigDecimal.valueOf(0)){
            throw new SaldoInvalido("Saldo do cliente precisa ser zerado para excluir a conta");
        }
        conta.setStatusConta(StatusConta.EXCLUIDA);
        contaRepository.save(conta);
    }

    @Transactional(readOnly = true)
    public void buscarContaPorId(Long id) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
    }

    @Transactional
    public ContaUpdateRequest atualizarConta(Long id, ContaUpdateRequest request) {
        Conta conta = contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
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
                titularIds,
                conta.getStatusConta());
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
                titularIds,
                conta.getStatusConta());
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
                conta.getResponsavelId(),
                titularIds,
                conta.getStatusConta());
    }
}