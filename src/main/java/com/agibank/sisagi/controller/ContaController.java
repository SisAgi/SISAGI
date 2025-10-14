package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {
    private final ContaService contaService;

    // Endpoint para a criação de uma nova conta-corrente
    @PostMapping("/corrente")
    public ResponseEntity<ContaCorrenteResponse> criarContaCorrente(@Valid @RequestBody ContaCorrenteRequest contaCorrente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarContaCorrente(contaCorrente));
    }

    // Endpoint para a criação de uma nova conta poupança
    @PostMapping("/poupanca")
    public ResponseEntity<ContaPoupResponse> criarContaPoupanca(@Valid @RequestBody ContaPoupRequest contaPoupanca) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarContaPoupanca(contaPoupanca));
    }

    // Endpoint para a criação de uma nova conta jovem
    @PostMapping("/jovem")
    public ResponseEntity<ContaJovemResponse> criarContaJovem(@Valid @RequestBody ContaJovemRequest contaJovem){
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarContaJovem(contaJovem));
    }

    //Endpoint para a criação de uma nova conta global
    @PostMapping("/global")
    public ResponseEntity<ContaGlobalResponse> criarContaGlobal(@Valid @RequestBody ContaGlobalRequest contaGlobal){
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarContaGlobal(contaGlobal));
    }

    @GetMapping
    public ResponseEntity<List<Object>> listarContas() {
        List<Conta> contas = contaService.listarTodasContas();
        List<Object> contasResponse = contas.stream()
                .map(contaService::mapearContaParaResponse)
                .toList();
        return ResponseEntity.ok(contasResponse);
    }

    // Endpoint para buscar uma conta pelo ID
    @GetMapping("/{contaId}")
    public ResponseEntity<Object> getContaById(@PathVariable Long contaId) {
        Object contaResponse = contaService.buscarDetalhesContaPorId(contaId);
        return ResponseEntity.ok(contaResponse);
    }

    @GetMapping("/buscar-numero/{numeroConta}")
    public ResponseEntity<Object> getContaByNumeroConta(@PathVariable String numeroConta) {
        Object contaResponse = contaService.buscarDetalhesContaPorNumero(numeroConta);
        return ResponseEntity.ok(contaResponse);
    }

    @PutMapping("/desativar/{numeroConta}")
    public ResponseEntity<Object> desativarConta(
            @PathVariable String numeroConta,
            @Valid @RequestBody ValidarSenhaRequest request) throws Exception { // Add ValidarSenhaRequest to the method signature

        // Autenticação da senha para encerramento da conta
        if (!contaService.validarSenhaContaPorNumero(numeroConta, request.senha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Senha incorreta.");
        }

        Object conta = contaService.desativarConta(numeroConta);
        if (conta == null){
            throw new Exception();
        }
        return ResponseEntity.ok(conta);
    }

    // Endpoint para buscar todas as contas de um cliente por CPF
    @GetMapping("/buscar-por-cpf/{cpf}")
    public ResponseEntity<List<Object>> buscarContasPorCpf(@PathVariable String cpf) {
        List<Object> contas = contaService.buscarContasPorCpf(cpf);
        return ResponseEntity.ok(contas);
    }

    // Endpoint para validar senha da conta por ID
    @PostMapping("/{contaId}/validar-senha")
    public ResponseEntity<Map<String, Boolean>> validarSenhaConta(
            @PathVariable Long contaId,
            @Valid @RequestBody ValidarSenhaRequest request) {
        boolean senhaValida = contaService.validarSenhaConta(contaId, request.senha());
        Map<String, Boolean> response = new HashMap<>();
        response.put("senhaValida", senhaValida);
        return ResponseEntity.ok(response);
    }

    // Endpoint para validar senha da conta por número
    @PostMapping("/numero/{numeroConta}/validar-senha")
    public ResponseEntity<Map<String, Boolean>> validarSenhaContaPorNumero(
            @PathVariable String numeroConta,
            @Valid @RequestBody ValidarSenhaRequest request) {
        boolean senhaValida = contaService.validarSenhaContaPorNumero(numeroConta, request.senha());
        Map<String, Boolean> response = new HashMap<>();
        response.put("senhaValida", senhaValida);
        return ResponseEntity.ok(response);
    }
}
