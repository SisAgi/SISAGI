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
import java.time.LocalDateTime;
import java.util.List;
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
    public TransacaoResponse processarTransacao(TransacaoRequest request, Long gerenteExecutorId) {
        // Validação e Carregamento de Entidades
        Gerente gerente = gerenteRepository.findById(gerenteExecutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Gerente executor não encontrado."));

        Conta contaPrincipal = contaRepository.findById(request.contaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta principal/destino não encontrada."));

        BigDecimal valor = request.valor();
        TipoTransacao tipo = request.tipoTransacao();

        // Execução da Lógica da Operação
        if (tipo == TipoTransacao.DEPOSITO) {
            // Depósito: Apenas credita na conta principal/destino
            executarCredito(contaPrincipal, valor, tipo, gerente, request.motivoMovimentacao());
        } else if (tipo == TipoTransacao.SAQUE) {
            // Saque: Debita da conta principal/origem (Conta principal é a origem neste caso)
            executarDebito(contaPrincipal, valor, tipo, gerente, request.motivoMovimentacao());

            // Regra de Negócio: Saques acima de R$ 10 mil requerem motivo.
            if (valor.compareTo(BigDecimal.valueOf(10000.00)) > 0 && (request.motivoMovimentacao() == null || request.motivoMovimentacao().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saques acima de R$ 10.000,00 requerem o motivo da movimentação.");
            }
        } else if (tipo == TipoTransacao.TRANSFERENCIA_ENVIADA) {
            // Transferência: Debita da origem e credita no destino
            if (request.contaOrigemId() == null || request.contaDestinoId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transferências requerem IDs de Conta Origem e Conta Destino.");
            }

            Conta contaOrigem = contaRepository.findById(request.contaOrigemId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de origem não encontrada."));

            Conta contaDestino = contaRepository.findById(request.contaDestinoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta de destino não encontrada."));

            // Executa o débito na origem
            executarDebito(contaOrigem, valor, TipoTransacao.TRANSFERENCIA_ENVIADA, gerente, request.motivoMovimentacao());

            // Executa o crédito no destino (cria uma segunda transação para a conta destino)
            executarCredito(contaDestino, valor, TipoTransacao.TRANSFERENCIA_RECEBIDA, gerente, request.motivoMovimentacao());

            // A transação principal retorna a enviada (débito)
            return toResponse(transacaoRepository.findByNsUnico(contaOrigem.getTransacoes().stream().findFirst().get().getNsUnico()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de transação não suportado ou inválido.");
        }

        // Salva as mudanças no saldo da Conta
        contaRepository.save(contaPrincipal);

        // Retorna a transação criada (para Depósito e Saque)
        // Como a transação é criada no metodo de execução, aqui pode-se buscar a última transação
        // da conta para retornar a resposta correta (simplificado para fins didáticos).
        return new TransacaoResponse(null, null, tipo.getDescricao(), valor, LocalDateTime.now(), contaPrincipal.getId(), null, null, gerenteExecutorId, request.motivoMovimentacao());
    }

    private Transacao executarDebito(Conta conta, BigDecimal valor, TipoTransacao tipo, Gerente gerente, String motivo) {
        try {
            conta.debitar(valor);
        } catch (RuntimeException e) {
            // Propaga erro de saldo para o Controller
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        // Cria e salva o registro de transação
        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipoTransacao(tipo);
        transacao.setValor(valor);
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(motivo);
        // O @PrePersist cuidará de dataHora e nsUnico
        return transacaoRepository.save(transacao);
    }

    private Transacao executarCredito(Conta conta, BigDecimal valor, TipoTransacao tipo, Gerente gerente, String motivo) {
        conta.creditar(valor);

        // Cria e salva o registro de transação
        Transacao transacao = new Transacao();
        transacao.setConta(conta);
        transacao.setTipoTransacao(tipo);
        transacao.setValor(valor);
        transacao.setIdGerenteExecutor(gerente);
        transacao.setMotivoMovimentacao(motivo);
        // Regra de Negócio: Depósitos acima de R$ 10 mil requerem motivo.
        if (valor.compareTo(BigDecimal.valueOf(10000.00)) > 0 && (motivo == null || motivo.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Depósitos em dinheiro acima de R$ 10.000,00 requerem a origem da espécie.");
        }

        // O @PrePersist cuidará de dataHora e nsUnico
        return transacaoRepository.save(transacao);
    }

    public List<TransacaoResponse> buscarExtratoPorConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada."));

        // No modelo real, você precisaria de um relacionamento @OneToMany na Conta para buscar as transações.
        // Simulando a busca por ID da conta.
        // return transacaoRepository.findByContaId(contaId).stream().map(this::toResponse).collect(Collectors.toList());

        // Retornando uma lista vazia para compilação
        return List.of();
    }

    // Método de Mapeamento
    private TransacaoResponse toResponse(Transacao transacao) {
        return new TransacaoResponse(
                transacao.getId(),
                transacao.getNsUnico(),
                transacao.getTipoTransacao().getDescricao(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getConta().getId(),
                transacao.getContaOrigem() != null ? transacao.getContaOrigem().getId() : null,
                transacao.getContaDestino() != null ? transacao.getContaDestino().getId() : null,
                transacao.getIdGerenteExecutor().getId(),
                transacao.getMotivoMovimentacao()
        );
    }
}
