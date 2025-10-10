package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.exception.SaldoInvalido;
import com.agibank.sisagi.model.*;
import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.ContaRepository;
import jakarta.persistence.DiscriminatorValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {
    private final ContaRepository contaRepository;
    private final ClienteRepository clienteRepository;

    // Cria uma conta poupança
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
        contaCorrente.setStatusConta(StatusConta.ATIVA);
        contaRepository.save(contaCorrente);
        return maptoContaCorrenteResponse(contaCorrente);
    }

    // Cria uma conta poupança
    @Transactional
    public ContaPoupResponse criarContaPoupanca(ContaPoupRequest request) {

        List<Cliente> titulares = request.titularCpfs().stream()
                .map(cpf -> clienteRepository.findByCpf(cpf)
                        .orElseThrow(() -> new RecursoNaoEncontrado("Cliente não encontrado para o CPF: " + cpf)))
                .collect(Collectors.toList());

        Cliente titularPrincipal = titulares.get(0);
        SegmentoCliente segmento = definirSegmentoCliente(titularPrincipal.getSalarioMensal(), titularPrincipal.getPatrimonioEstimado());
        BigDecimal taxaManutencao = definirTaxaManutencao(segmento);

        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setNumeroConta(gerarNumeroContaUnico()); // Nova chamada
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setAgencia(request.agencia());
        contaPoupanca.setSenha(request.senha());
        contaPoupanca.setRendimento(contaPoupanca.getRendimento());
        contaPoupanca.setTitulares(new HashSet<>(titulares));
        contaPoupanca.setStatusConta(StatusConta.ATIVA);
        contaPoupanca.setDataAbertura(java.time.LocalDate.now());
        contaPoupanca.setSegmentoCliente(segmento);
        contaPoupanca.setTaxaManutencao(taxaManutencao);

        contaRepository.save(contaPoupanca);
        return maptoContaPoupancaResponse(contaPoupanca);
    }

    // Cria uma conta jovem
    @Transactional
    public ContaJovemResponse criarContaJovem(ContaJovemRequest request) {
        Conta responsavel = contaRepository.findById(request.responsavelId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente responsável não encontrada"));

        Set<Cliente> titulares = new HashSet<>(clienteRepository.findAllById(request.titularIds()));
        if (titulares.isEmpty()) {
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

    // Cria a conta global
    @Transactional
    public ContaGlobalResponse criarContaGlobal(ContaGlobalRequest request) {

        List<Cliente> titulares = clienteRepository.findAllById(request.titularIds());
        if (titulares.size() != request.titularIds().size()) {
            throw new RecursoNaoEncontrado("Um ou mais IDs de clientes são inválidos");
        }
        ContaGlobal contaGlobal = new ContaGlobal();
        contaGlobal.setNumeroConta(request.numeroConta());
        contaGlobal.setSaldo(BigDecimal.ZERO);
        contaGlobal.setSaldoDolar(BigDecimal.ZERO);
        contaGlobal.setCodigoSwift("AGIBRSP201");
        contaGlobal.setAgencia(request.agencia());
        contaGlobal.setTitulares(new HashSet<>(titulares));
        contaGlobal.setStatusConta(StatusConta.ATIVA);
        contaRepository.save(contaGlobal);
        return maptoContaGlobalResponse(contaGlobal);
    }

    // Desativa a conta impedindo que a mesma realize transações, porém ainda estando presente no banco de dados como forma de consulta
    @Transactional
    public void desativarConta(Long Id) {

        Conta conta = contaRepository.findById(Id)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new SaldoInvalido("Saldo do cliente precisa ser zerado para excluir a conta");
        }
        conta.setStatusConta(StatusConta.EXCLUIDA);
        contaRepository.save(conta);
    }

    @Transactional(readOnly = true)
    public void buscarContaPorId(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
    }

    @Transactional(readOnly = true)
    public Object buscarDetalhesContaPorNumero(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada com número: " + numeroConta));

        if (conta instanceof ContaCorrente cc) {
            return maptoContaCorrenteResponse(cc);
        } else if (conta instanceof ContaPoupanca pp) {
            return maptoContaPoupancaResponse(pp);
        } else if (conta instanceof ContaJovem cj) {
            return maptoContaJovemResponse(cj);
        } else if (conta instanceof ContaGlobal cg) {
            return maptoContaGlobalResponse(cg);
        }

        throw new RecursoNaoEncontrado("Tipo de conta desconhecido para o número: " + numeroConta);
    }

    @Transactional
    public ContaUpdateRequest atualizarConta(Long contaId, ContaUpdateRequest request) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
        conta.setAgencia(request.agencia());
        Conta contaAtualizada = contaRepository.save(conta);
        return new ContaUpdateRequest(contaAtualizada.getAgencia(), contaAtualizada.getNumeroConta(), request.cpf());
    }

    private String gerarNumeroContaUnico() {
        String numeroContaGerado;
        do {
            // Gera um número aleatório de 6 dígitos
            long numeroAleatorio = ThreadLocalRandom.current().nextLong(100000, 1000000);
            // Formata o número com um dígito verificador simples (ex: último dígito)
            int digitoVerificador = (int) (numeroAleatorio % 10);
            numeroContaGerado = String.format("%06d-%d", numeroAleatorio, digitoVerificador);
        } while (contaRepository.findByNumeroConta(numeroContaGerado).isPresent());
        return numeroContaGerado;
    }

    private SegmentoCliente definirSegmentoCliente(BigDecimal rendaMensal, BigDecimal patrimonio) {
        if (rendaMensal != null && rendaMensal.compareTo(new BigDecimal("10000")) > 0 ||
                patrimonio != null && patrimonio.compareTo(new BigDecimal("200000")) > 0) {
            return SegmentoCliente.PREMIUM;
        } else if (rendaMensal != null && rendaMensal.compareTo(new BigDecimal("3000")) > 0 ||
                patrimonio != null && patrimonio.compareTo(new BigDecimal("100000")) >= 0) {
            return SegmentoCliente.ADVANCED;
        } else {
            return SegmentoCliente.CLASS;
        }
    }

    private BigDecimal definirTaxaManutencao(SegmentoCliente segmento) {
        switch (segmento) {
            case CLASS:
                return new BigDecimal("3.90");
            case ADVANCED:
                return new BigDecimal("8.90");
            case PREMIUM:
                return new BigDecimal("19.90");
            default:
                return BigDecimal.ZERO; // Valor padrão para casos inesperados
        }
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
                conta.getStatusConta(),
                getTipoConta(conta));
    }

    // Tem a função de mapear os campos da entidade conta poupança para o seu respectivo DTO de resposta
    public ContaPoupResponse maptoContaPoupancaResponse(ContaPoupanca conta) {
        Set<String> titularCpfs = conta.getTitulares().stream()
                .map(Cliente::getCpf)
                .collect(Collectors.toSet());
        return new ContaPoupResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getDataAniversario(),
                BigDecimal.valueOf(conta.getRendimento()),
                getTipoConta(conta),
                titularCpfs,
                conta.getStatusConta(),
                conta.getSegmentoCliente());
    }

    // Tem a função de mapear os campos da entidade conta jovem para o seu respectivo DTO de resposta
    public ContaJovemResponse maptoContaJovemResponse(ContaJovem conta) {
        Set<Long> titularIds = conta.getTitulares().stream()
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        return new ContaJovemResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getResponsavelId().getId(),
                titularIds,
                conta.getStatusConta(),
                getTipoConta(conta));
    }

    // Tem a função de mapear os campos da entidade conta global para o seu respectivo DTO de resposta
    public ContaGlobalResponse maptoContaGlobalResponse(ContaGlobal conta) {
        Set<Long> titularIds = conta.getTitulares().stream()
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        return new ContaGlobalResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getSaldoDolar(),
                conta.getCodigoSwift(),
                titularIds,
                conta.getStatusConta(),
                getTipoConta(conta));

    }

    // Tem a função de buscar os detalhes de uma conta específica
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

    // Descobre o tipo da conta através do discriminator value
    private static String getTipoConta(Conta conta) {
        DiscriminatorValue discriminatorValue = conta.getClass().getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null) {
            return discriminatorValue.value();
        } else {
            return "Tipo desconhecido";
        }
    }
}