package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.TransacaoRequest;
import com.agibank.sisagi.dto.TransacaoResponse;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.Gerente;
import com.agibank.sisagi.model.Transacao;
import com.agibank.sisagi.model.enums.TipoTransacao;
import com.agibank.sisagi.repository.ContaRepository;
import com.agibank.sisagi.repository.GerenteRepository;
import com.agibank.sisagi.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final GerenteRepository gerenteRepository;

    @Autowired
    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository, GerenteRepository gerenteRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.gerenteRepository = gerenteRepository;
    }

    @Transactional
    public TransacaoResponse realizarTransferencia(TransacaoRequest dto, Long gerenteExecutorId) {
        // Validação básica
        if (dto.contaOrigemId() == null || dto.contaDestinoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transferências requerem Conta de Origem e Conta de Destino.");
        }

        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta contaOrigem = contaRepository.findById(dto.contaOrigemId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada."));

        Conta contaDestino = contaRepository.findById(dto.contaDestinoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada."));

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Cria a transação de débito e salva
        Transacao debito = executarDebito(contaOrigem, dto.valor(), TipoTransacao.TRANSFERENCIA_ENVIADA, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        debito.setContaOrigem(contaOrigem);
        debito.setContaDestino(contaDestino);
        transacaoRepository.save(debito);

        // Cria a transação de crédito e salva
        Transacao credito = executarCredito(contaDestino, dto.valor(), TipoTransacao.TRANSFERENCIA_RECEBIDA, gerente, dto.motivoMovimentacao(), nsuDaOperacao);
        credito.setContaOrigem(contaOrigem);
        credito.setContaDestino(contaDestino);
        transacaoRepository.save(credito);

        // Atualiza saldos após as operações
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        return toResponse(debito);
    }

    @Transactional
    public TransacaoResponse realizarDeposito(TransacaoRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

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

    @Transactional
    public TransacaoResponse realizarSaque(TransacaoRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

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

    public List<TransacaoResponse> buscarExtratoPorConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        List<Transacao> transacoes = transacaoRepository.findByConta(conta);

        return transacoes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private TransacaoResponse toResponse(Transacao transacao) {
        Long contaOrigemId = (transacao.getContaOrigem() != null) ? transacao.getContaOrigem().getId() : null;
        String numeroContaOrigem = (transacao.getContaOrigem() != null) ? transacao.getContaOrigem().getNumeroConta() : null;

        Long contaDestinoId = (transacao.getContaDestino() != null) ? transacao.getContaDestino().getId() : null;
        String numeroContaDestino = (transacao.getContaDestino() != null) ? transacao.getContaDestino().getNumeroConta() : null;

        String nomeGerenteExecutor = (transacao.getIdGerenteExecutor() != null) ? transacao.getIdGerenteExecutor().getNome() : null;

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