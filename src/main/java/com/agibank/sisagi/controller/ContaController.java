package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Object> desativarConta(@PathVariable String numeroConta) throws Exception {
       Object conta = contaService.desativarConta(numeroConta);
       if (conta == null){
           throw new Exception();
       }
       return ResponseEntity.ok(conta);
    }
}
