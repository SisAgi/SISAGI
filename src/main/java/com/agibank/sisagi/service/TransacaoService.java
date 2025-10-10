package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.DepositoRequest;
import com.agibank.sisagi.dto.SaqueRequest;
import com.agibank.sisagi.dto.TransacaoResponse;
import com.agibank.sisagi.dto.TransferenciaRequest;
import com.agibank.sisagi.exception.ContaInvalida;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.Gerente;
import com.agibank.sisagi.model.Transacao;
import com.agibank.sisagi.model.enums.StatusConta;
import com.agibank.sisagi.model.enums.TipoTransacao;
import com.agibank.sisagi.repository.ContaRepository;
import com.agibank.sisagi.repository.GerenteRepository;
import com.agibank.sisagi.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final GerenteRepository gerenteRepository;

    // Realiza uma transferência entre contas
    @Transactional
    public TransacaoResponse realizarTransferencia(TransferenciaRequest dto, Long gerenteExecutorId) {
        // Validação básica
        if (dto.contaOrigemId() == null || dto.contaDestinoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transferências requerem Conta de Origem e Conta de Destino.");
        }

        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta contaOrigem = contaRepository.findById(dto.contaOrigemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada."));

        if (contaOrigem.getStatusConta() == StatusConta.EXCLUIDA){
            throw new ContaInvalida("Conta origem está excluida");
        }

        Conta contaDestino = contaRepository.findById(dto.contaDestinoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada."));

        if (contaDestino.getStatusConta() == StatusConta.EXCLUIDA){
            throw new ContaInvalida("Conta destino está excluida");
        }

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Cria a transação de débito e salva
        Transacao debito = executarDebito(contaOrigem, dto.valor(), TipoTransacao.TRANSFERENCIA, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        debito.setContaOrigem(contaOrigem);
        debito.setContaDestino(contaDestino);
        transacaoRepository.save(debito);

        // Cria a transação de crédito e salva
        Transacao credito = executarCredito(contaDestino, dto.valor(), TipoTransacao.TRANSFERENCIA, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        credito.setContaOrigem(contaOrigem);
        credito.setContaDestino(contaDestino);
        transacaoRepository.save(credito);

        // Atualiza saldos após as operações
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        return toResponse(debito);
    }

    // Realiza um depósito para uma conta específica
    @Transactional
    public TransacaoResponse realizarDeposito(DepositoRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        if (conta.getStatusConta() == StatusConta.EXCLUIDA){
            throw new ContaInvalida("Conta origem está excluida");
        }

        // Lógica de negócio: Depósitos acima de R$ 10 mil requerem motivo.
        if (dto.valor().compareTo(BigDecimal.valueOf(10000.00)) > 0 && (dto.motivoMovimentacao() == null || dto.motivoMovimentacao().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Depósitos em dinheiro acima de R$ 10.000,00 requerem a origem da espécie.");
        }

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        Transacao transacao = executarCredito(conta, dto.valor(), TipoTransacao.DEPOSITO, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        transacao.setContaDestino(conta);
        transacaoRepository.save(transacao);

        contaRepository.save(conta);

        return toResponse(transacao);
    }

    // Realiza um saque
    @Transactional
    public TransacaoResponse realizarSaque(SaqueRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        if (conta.getStatusConta() == StatusConta.EXCLUIDA){
            throw new ContaInvalida("Conta origem está excluida");
        }

        // Lógica de negócio: Saques acima de R$ 10 mil requerem motivo.
        if (dto.valor().compareTo(BigDecimal.valueOf(10000.00)) > 0 && (dto.motivoMovimentacao() == null || dto.motivoMovimentacao().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saques acima de R$ 10.000,00 requerem o motivo da movimentação.");
        }

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        Transacao transacao = executarDebito(conta, dto.valor(), TipoTransacao.SAQUE, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        transacao.setContaOrigem(conta);
        transacaoRepository.save(transacao);

        contaRepository.save(conta);

        return toResponse(transacao);
    }

    // Exibe extrato de conta
    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarExtratoPorConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        List<Transacao> transacoes = transacaoRepository.findByConta(conta);

        return transacoes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    // Métodos utilitários de débito e crédito
    private Transacao executarDebito(Conta conta, BigDecimal valor, TipoTransacao tipo, Gerente gerente, String motivo, String nsUnico) {
        try {
            conta.debitar(valor);
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipoTransacao(tipo);
        transacao.setValor(valor.negate());
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(motivo);
        transacao.setNsUnico(nsUnico);

        return transacao;
    }

    private Transacao executarCredito(Conta conta, BigDecimal valor, TipoTransacao tipo, Gerente gerente, String motivo, String nsUnico) {
        conta.creditar(valor);

        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipoTransacao(tipo);
        transacao.setValor(valor);
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(motivo);
        transacao.setNsUnico(nsUnico);

        return transacao;
    }

    // Tem a função de mapear os campos da entidade para um DTO de resposta
    private TransacaoResponse toResponse(Transacao transacao) {
        Long contaOrigemId = (transacao.getContaOrigem() != null) ? transacao.getContaOrigem().getId() : null;
        String numeroContaOrigem = (transacao.getContaOrigem() != null) ? transacao.getContaOrigem().getNumeroConta() : null;

        Long contaDestinoId = (transacao.getContaDestino() != null) ? transacao.getContaDestino().getId() : null;
        String numeroContaDestino = (transacao.getContaDestino() != null) ? transacao.getContaDestino().getNumeroConta() : null;

        String nomeGerenteExecutor = (transacao.getIdGerenteExecutor() != null) ? transacao.getIdGerenteExecutor().getNomeCompleto() : null;

        assert transacao.getIdGerenteExecutor() != null;
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getNsUnico(),
                contaOrigemId,
                numeroContaOrigem,
                contaDestinoId,
                numeroContaDestino,
                transacao.getIdGerenteExecutor().getId(),
                nomeGerenteExecutor,
                transacao.getMotivoMovimentacao()
        );
    }
}