package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.ContaInvalida;
import com.agibank.sisagi.exception.SaldoInvalido;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.ContaGlobal;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final GerenteRepository gerenteRepository;
    private final ExchangeRateService exchangeRateService;

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

    // Converte saldo de reais para dólares
    @Transactional
    public ConversaoMoedaResponse converterReaisParaDolares(ConversaoMoedaRequest dto, Long gerenteExecutorId) {
        // Busca o gerente
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        // Busca a conta
        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        // Verifica se é conta global
        if (!(conta instanceof ContaGlobal)) {
            throw new ContaInvalida("Somente contas globais podem realizar conversão de moeda.");
        }

        ContaGlobal contaGlobal = (ContaGlobal) conta;

        // Verifica se a conta está ativa
        if (contaGlobal.getStatusConta() == StatusConta.EXCLUIDA) {
            throw new ContaInvalida("Conta está excluída.");
        }

        // Verifica se tem saldo suficiente
        if (contaGlobal.getSaldo().compareTo(dto.valorReais()) < 0) {
            throw new SaldoInvalido("Saldo insuficiente para realizar a conversão.");
        }

        // Verifica se o valor é maior que zero
        if (dto.valorReais().compareTo(BigDecimal.ZERO) <= 0) {
            throw new SaldoInvalido("O valor a ser convertido deve ser maior que zero.");
        }

        // Obtém a cotação atual
        BigDecimal cotacao = exchangeRateService.getCotacaoAtual();
        
        // Converte o valor para dólares
        BigDecimal valorDolares = exchangeRateService.converterRealParaDolar(dto.valorReais());

        // Debita o valor em reais da conta
        contaGlobal.debitar(dto.valorReais());
        
        // Credita o valor em dólares
        BigDecimal saldoDolarAtual = contaGlobal.getSaldoDolar() != null ? contaGlobal.getSaldoDolar() : BigDecimal.ZERO;
        contaGlobal.setSaldoDolar(saldoDolarAtual.add(valorDolares));
        
        // Atualiza a cotação atual da conta
        contaGlobal.setCotacaoAtual(cotacao);

        // Gera NSU único
        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Cria a transação
        Transacao transacao = new Transacao();
        transacao.setConta(contaGlobal);
        transacao.setContaOrigem(contaGlobal);
        transacao.setTipoTransacao(TipoTransacao.CONVERSAO_MOEDA);
        transacao.setValor(dto.valorReais().negate()); // Negativo porque está saindo da conta em reais
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(dto.motivoMovimentacao());
        transacao.setNsUnico(nsuDaOperacao);

        // Salva a transação e a conta
        transacaoRepository.save(transacao);
        contaRepository.save(contaGlobal);

        return new ConversaoMoedaResponse(
                transacao.getId(),
                contaGlobal.getId(),
                contaGlobal.getNumeroConta(),
                dto.valorReais(),
                valorDolares,
                cotacao,
                contaGlobal.getSaldo(),
                contaGlobal.getSaldoDolar(),
                LocalDateTime.now(),
                nsuDaOperacao
        );
    }

    // Realiza um saque internacional (em dólares)
    @Transactional
    public TransacaoResponse realizarSaqueInternacional(SaqueInternacionalRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        // Verifica se é conta global
        if (!(conta instanceof ContaGlobal)) {
            throw new ContaInvalida("Somente contas globais podem realizar saques internacionais.");
        }

        ContaGlobal contaGlobal = (ContaGlobal) conta;

        if (contaGlobal.getStatusConta() == StatusConta.EXCLUIDA) {
            throw new ContaInvalida("Conta está excluída.");
        }

        // Verifica se tem saldo em dólar suficiente
        BigDecimal saldoDolar = contaGlobal.getSaldoDolar() != null ? contaGlobal.getSaldoDolar() : BigDecimal.ZERO;
        if (saldoDolar.compareTo(dto.valorDolares()) < 0) {
            throw new SaldoInvalido("Saldo em dólares insuficiente para realizar o saque.");
        }

        // Lógica de negócio: Saques internacionais acima de $ 5000 requerem motivo
        if (dto.valorDolares().compareTo(BigDecimal.valueOf(5000.00)) > 0 
            && (dto.motivoMovimentacao() == null || dto.motivoMovimentacao().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Saques internacionais acima de $ 5.000,00 requerem o motivo da movimentação.");
        }

        // Debita o valor em dólares
        contaGlobal.setSaldoDolar(saldoDolar.subtract(dto.valorDolares()));

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Cria a transação
        Transacao transacao = new Transacao();
        transacao.setConta(contaGlobal);
        transacao.setContaOrigem(contaGlobal);
        transacao.setTipoTransacao(TipoTransacao.SAQUE_INTERNACIONAL);
        transacao.setValor(dto.valorDolares().negate()); // Negativo para indicar saída
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(dto.motivoMovimentacao());
        transacao.setNsUnico(nsuDaOperacao);

        transacaoRepository.save(transacao);
        contaRepository.save(contaGlobal);

        return toResponse(transacao);
    }

    // Realiza um depósito internacional (em dólares)
    @Transactional
    public TransacaoResponse realizarDepositoInternacional(DepositoInternacionalRequest dto, Long gerenteExecutorId) {
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta conta = contaRepository.findById(dto.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        // Verifica se é conta global
        if (!(conta instanceof ContaGlobal)) {
            throw new ContaInvalida("Somente contas globais podem realizar depósitos internacionais.");
        }

        ContaGlobal contaGlobal = (ContaGlobal) conta;

        if (contaGlobal.getStatusConta() == StatusConta.EXCLUIDA) {
            throw new ContaInvalida("Conta está excluída.");
        }

        // Lógica de negócio: Depósitos internacionais acima de $ 5000 requerem motivo
        if (dto.valorDolares().compareTo(BigDecimal.valueOf(5000.00)) > 0 
            && (dto.motivoMovimentacao() == null || dto.motivoMovimentacao().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Depósitos internacionais acima de $ 5.000,00 requerem a origem da espécie.");
        }

        // Credita o valor em dólares
        BigDecimal saldoDolar = contaGlobal.getSaldoDolar() != null ? contaGlobal.getSaldoDolar() : BigDecimal.ZERO;
        contaGlobal.setSaldoDolar(saldoDolar.add(dto.valorDolares()));

        String nsuDaOperacao = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        // Cria a transação
        Transacao transacao = new Transacao();
        transacao.setConta(contaGlobal);
        transacao.setContaDestino(contaGlobal);
        transacao.setTipoTransacao(TipoTransacao.DEPOSITO_INTERNACIONAL);
        transacao.setValor(dto.valorDolares()); // Positivo para indicar entrada
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(dto.motivoMovimentacao());
        transacao.setNsUnico(nsuDaOperacao);

        transacaoRepository.save(transacao);
        contaRepository.save(contaGlobal);

        return toResponse(transacao);
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