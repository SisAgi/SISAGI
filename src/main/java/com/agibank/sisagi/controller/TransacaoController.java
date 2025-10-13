package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transacoes")
@RequiredArgsConstructor
public class TransacaoController {
    
    private final TransacaoService transacaoService;

    // Endpoint para realizar uma transferência entre contas
    @PostMapping("/transferencia")
    public ResponseEntity<TransacaoResponse> realizarTransferencia(
            @Valid @RequestBody TransferenciaRequest request,
            @RequestParam Long gerenteExecutorId) throws InterruptedException {
        TransacaoResponse response = transacaoService.realizarTransferencia(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para realizar um depósito em uma conta
    @PostMapping("/deposito")
    public ResponseEntity<TransacaoResponse> realizarDeposito(
            @Valid @RequestBody DepositoRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarDeposito(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para realizar um saque em uma conta
    @PostMapping("/saque")
    public ResponseEntity<TransacaoResponse> realizarSaque(
            @Valid @RequestBody SaqueRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarSaque(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para buscar o extrato de uma conta
    @GetMapping("/extrato/{contaId}")
    public ResponseEntity<List<TransacaoResponse>> buscarExtratoPorConta(@PathVariable Long contaId) {
        List<TransacaoResponse> extrato = transacaoService.buscarExtratoPorConta(contaId);
        return ResponseEntity.ok(extrato);
    }

    // Endpoint para converter reais para dólares
    @PostMapping("/conversao-moeda")
    public ResponseEntity<ConversaoMoedaResponse> converterReaisParaDolares(
            @Valid @RequestBody ConversaoMoedaRequest request,
            @RequestParam Long gerenteExecutorId) {
        ConversaoMoedaResponse response = transacaoService.converterReaisParaDolares(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para realizar um saque internacional (em dólares)
    @PostMapping("/saque-internacional")
    public ResponseEntity<TransacaoResponse> realizarSaqueInternacional(
            @Valid @RequestBody SaqueInternacionalRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarSaqueInternacional(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para realizar um depósito internacional (em dólares)
    @PostMapping("/deposito-internacional")
    public ResponseEntity<TransacaoResponse> realizarDepositoInternacional(
            @Valid @RequestBody DepositoInternacionalRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarDepositoInternacional(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

