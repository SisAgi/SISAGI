package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.email.EmailService;
import com.agibank.sisagi.exception.DebitosAtivos;
import com.agibank.sisagi.exception.RecursoNaoEncontrado;
import com.agibank.sisagi.exception.SaldoInvalido;
import com.agibank.sisagi.model.*;
import com.agibank.sisagi.model.enums.SegmentoCliente;
import com.agibank.sisagi.model.enums.StatusConta;
import com.agibank.sisagi.model.enums.StatusDebito;
import com.agibank.sisagi.repository.ClienteRepository;
import com.agibank.sisagi.repository.ContaRepository;
import jakarta.persistence.DiscriminatorValue;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Cria uma conta poupança
    @Transactional
    public ContaCorrenteResponse criarContaCorrente(ContaCorrenteRequest request) {
        // Busca os clientes pelo CPF
        List<Cliente> titulares = request.titularCpfs().stream()
                .map(cpf -> clienteRepository.findByCpf(cpf)
                        .orElseThrow(() -> new RecursoNaoEncontrado("Cliente não encontrado para o CPF: " + cpf)))
                .toList();

        Cliente titularPrincipal = titulares.getFirst();
        SegmentoCliente segmento = definirSegmentoCliente(titularPrincipal.getRendaMensal(), titularPrincipal.getPatrimonioEstimado());
        BigDecimal taxaManutencao = definirTaxaManutencao(segmento);
        BigDecimal limiteChequeEspecial = definirLimiteChequeEspecial(
                titularPrincipal.getRendaMensal(),
                titularPrincipal.getPossuiRestricoesBancarias()
        );

        // 2. Cria a conta-corrente
        ContaCorrente contaCorrente = new ContaCorrente();
        contaCorrente.setNumeroConta(gerarNumeroContaUnico());
        contaCorrente.setAgencia(request.agencia());
        contaCorrente.setSaldo(BigDecimal.ZERO);
        contaCorrente.setSenha(passwordEncoder.encode(request.senha()));
        contaCorrente.setLimiteChequeEspecial(limiteChequeEspecial);
        contaCorrente.setTitulares(new HashSet<>(titulares));
        contaCorrente.setStatusConta(StatusConta.ATIVA);
        contaCorrente.setDataAbertura(LocalDate.now());
        contaCorrente.setSegmentoCliente(segmento);
        contaCorrente.setTaxaManutencao(taxaManutencao);
        contaRepository.save(contaCorrente);

        return maptoContaCorrenteResponse(contaCorrente);
    }

    // Cria uma conta poupança
    @Transactional
    public ContaPoupResponse criarContaPoupanca(ContaPoupRequest request) {

        List<Cliente> titulares = request.titularCpfs().stream()
                .map(cpf -> clienteRepository.findByCpf(cpf)
                        .orElseThrow(() -> new RecursoNaoEncontrado("Cliente não encontrado para o CPF: " + cpf)))
                .toList();

        Cliente titularPrincipal = titulares.getFirst();
        SegmentoCliente segmento = definirSegmentoCliente(titularPrincipal.getRendaMensal(), titularPrincipal.getPatrimonioEstimado());
        BigDecimal taxaManutencao = definirTaxaManutencao(segmento);

        ContaPoupanca contaPoupanca = new ContaPoupanca();
        contaPoupanca.setNumeroConta(gerarNumeroContaUnico()); // Nova chamada
        contaPoupanca.setSaldo(BigDecimal.ZERO);
        contaPoupanca.setAgencia(request.agencia());
        contaPoupanca.setSenha(passwordEncoder.encode(request.senha()));
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
        // Busca a conta do responsável pelo número da conta
        Conta contaDoResponsavel = contaRepository.findByNumeroConta(request.numeroContaResponsavel())
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta do responsável não encontrada."));

        // As contas responsáveis só podem ser Corrente ou Poupança
        if (!(contaDoResponsavel instanceof ContaCorrente || contaDoResponsavel instanceof ContaPoupanca)) {
            throw new IllegalArgumentException("A conta responsável deve ser do tipo Corrente ou Poupança.");
        }

        // Busca os clientes titulares pelo CPF
        List<Cliente> titulares = request.titularCpfs().stream()
                .map(cpf -> clienteRepository.findByCpf(cpf)
                        .orElseThrow(() -> new RecursoNaoEncontrado("Cliente titular não encontrado para o CPF: " + cpf)))
                .toList();

        Cliente titularPrincipal = titulares.getFirst();
        SegmentoCliente segmento = definirSegmentoCliente(titularPrincipal.getRendaMensal(), titularPrincipal.getPatrimonioEstimado());
        BigDecimal taxaManutencao = definirTaxaManutencao(segmento);

        // Cria e configura a Conta Jovem
        ContaJovem contaJovem = new ContaJovem();
        contaJovem.setNumeroConta(gerarNumeroContaUnico());
        contaJovem.setSaldo(BigDecimal.ZERO);
        contaJovem.setAgencia(request.agencia());
        contaJovem.setSenha(passwordEncoder.encode(request.senha()));
        contaJovem.setResponsavelContaId(contaDoResponsavel);
        contaJovem.setTitulares(new HashSet<>(titulares));
        contaJovem.setStatusConta(StatusConta.ATIVA);
        contaJovem.setDataAbertura(LocalDate.now());
        contaJovem.setSegmentoCliente(segmento);
        contaJovem.setTaxaManutencao(taxaManutencao);
        contaRepository.save(contaJovem);

        return maptoContaJovemResponse(contaJovem);
    }

    // Cria a conta global
    @Transactional
    public ContaGlobalResponse criarContaGlobal(ContaGlobalRequest request) {
        // 1. Busca os clientes pelo CPF
        List<Cliente> titulares = request.titularCpfs().stream()
                .map(cpf -> clienteRepository.findByCpf(cpf)
                        .orElseThrow(() -> new RecursoNaoEncontrado("Cliente não encontrado para o CPF: " + cpf)))
                .toList();

        Cliente titularPrincipal = titulares.getFirst();
        SegmentoCliente segmento = definirSegmentoCliente(titularPrincipal.getRendaMensal(), titularPrincipal.getPatrimonioEstimado());
        BigDecimal taxaManutencao = definirTaxaManutencao(segmento);

        // 2. Cria e configura a Conta Global
        ContaGlobal contaGlobal = new ContaGlobal();
        contaGlobal.setNumeroConta(gerarNumeroContaUnico());
        contaGlobal.setSaldo(BigDecimal.ZERO);
        contaGlobal.setSaldoDolar(BigDecimal.ZERO);
        contaGlobal.setAgencia(request.agencia());
        contaGlobal.setCodigoSwift("AGIBRSP" + contaGlobal.getAgencia());
        contaGlobal.setSenha(passwordEncoder.encode(request.senha()));
        contaGlobal.setTitulares(new HashSet<>(titulares));
        contaGlobal.setStatusConta(StatusConta.ATIVA);
        contaGlobal.setDataAbertura(LocalDate.now());
        contaGlobal.setSegmentoCliente(segmento);
        contaGlobal.setTaxaManutencao(taxaManutencao);
        contaRepository.save(contaGlobal);

        return maptoContaGlobalResponse(contaGlobal);
    }

    // Desativa a conta impedindo que a mesma realize transações, porém ainda estando presente no banco de dados como forma de consulta
    @Transactional
    public Object desativarConta(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));

        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new SaldoInvalido("Saldo do cliente precisa ser zerado para excluir a conta");
        }

        List<DebitoAutomatico> debitosAtivos = conta.getDebitoAutomaticos()
                .stream()
                .filter(n -> n.getStatus().equals(StatusDebito.ATIVO))
                .toList();

        if (!debitosAtivos.isEmpty()) {
            throw new DebitosAtivos("Conta possui débitos automáticos ativos");
        }

        conta.setStatusConta(StatusConta.EXCLUIDA);
        contaRepository.save(conta);
        conta.getTitulares().stream().findFirst().ifPresent(titular -> emailService.notificarCancelamentoConta(titular, numeroConta));

        return mapearContaParaResponse(conta);
    }

    @Transactional(readOnly = true)
    public Object buscarDetalhesContaPorId(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada com ID: " + contaId));
        return mapearContaParaResponse(conta);
    }

    @Transactional(readOnly = true)
    public Object buscarDetalhesContaPorNumero(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada com número: " + numeroConta));
        return mapearContaParaResponse(conta);
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

    public SegmentoCliente definirSegmentoCliente(BigDecimal rendaMensal, BigDecimal patrimonio) {
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

    public BigDecimal definirTaxaManutencao(SegmentoCliente segmento) {
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

    public BigDecimal definirLimiteChequeEspecial(BigDecimal rendaMensal, Boolean possuiRestricoes) {
        if (possuiRestricoes != null && possuiRestricoes) {
            return BigDecimal.ZERO;
        }

        if (rendaMensal != null && rendaMensal.compareTo(BigDecimal.ZERO) > 0) {
            // Calcula 20% da renda: renda * 0.20
            BigDecimal limite = rendaMensal.multiply(new BigDecimal("0.20"));
            return limite.setScale(2, RoundingMode.HALF_UP); // Arredonda para 2 casas decimais
        }

        return BigDecimal.ZERO; // Caso a renda seja nula ou zero
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

    @Transactional(readOnly = true)
    public List<Conta> listarTodasContas() {
        return contaRepository.findAll();
    }

    // Metodo genérico para mapear qualquer tipo de conta para seu DTO
    public Object mapearContaParaResponse(Conta conta) {
        if (conta instanceof ContaCorrente cc) {
            return maptoContaCorrenteResponse(cc);
        } else if (conta instanceof ContaPoupanca cp) {
            return maptoContaPoupancaResponse(cp);
        } else if (conta instanceof ContaJovem cj) {
            return maptoContaJovemResponse(cj);
        } else if (conta instanceof ContaGlobal cg) {
            return maptoContaGlobalResponse(cg);
        }
        return null;
    }

    public ContaCorrenteResponse maptoContaCorrenteResponse(ContaCorrente conta) {
        Set<String> titularCpfs = conta.getTitulares().stream()
                .map(Cliente::getCpf)
                .collect(Collectors.toSet());

        return new ContaCorrenteResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getLimiteChequeEspecial(),
                titularCpfs,
                conta.getStatusConta(),
                getTipoConta(conta),
                conta.getSegmentoCliente()
        );
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
        Set<String> titularCpfs = conta.getTitulares().stream()
                .map(Cliente::getCpf)
                .collect(Collectors.toSet());
        return new ContaJovemResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getResponsavelContaId().getId(),
                titularCpfs,
                conta.getStatusConta(),
                getTipoConta(conta),
                conta.getSegmentoCliente());
    }

    // Tem a função de mapear os campos da entidade conta global para o seu respectivo DTO de resposta
    public ContaGlobalResponse maptoContaGlobalResponse(ContaGlobal conta) {
        Set<String> titularCpfs = conta.getTitulares().stream()
                .map(Cliente::getCpf)
                .collect(Collectors.toSet());
        return new ContaGlobalResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getAgencia(),
                conta.getSaldo(),
                conta.getSaldoDolar(),
                conta.getCodigoSwift(),
                titularCpfs,
                conta.getStatusConta(),
                getTipoConta(conta),
                conta.getSegmentoCliente());
    }

    public BigDecimal consultarSaldo(String numeroConta) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));
        return conta.getSaldo();
    }

    @Transactional(readOnly = true)
    public List<Object> buscarContasPorCpf(String cpf) {
        List<Conta> contas = contaRepository.findByTitularCpf(cpf);

        if (contas.isEmpty()) {
            throw new RecursoNaoEncontrado("Nenhuma conta encontrada para o CPF: " + cpf);
        }

        return contas.stream()
                .map(this::mapearContaParaResponse)
                .collect(Collectors.toList());
    }

    // Valida a senha de uma conta
    @Transactional(readOnly = true)
    public boolean validarSenhaConta(Long contaId, String senha) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));

        return passwordEncoder.matches(senha, conta.getSenha());
    }

    // Valida a senha de uma conta por número
    @Transactional(readOnly = true)
    public boolean validarSenhaContaPorNumero(String numeroConta, String senha) {
        Conta conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontrado("Conta não encontrada"));

        return passwordEncoder.matches(senha, conta.getSenha());
    }
}