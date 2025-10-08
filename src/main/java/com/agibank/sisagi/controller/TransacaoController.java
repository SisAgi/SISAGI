package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.TransacaoRequest;
import com.agibank.sisagi.dto.TransacaoResponse;
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

    @PostMapping("/transferencia")
    public ResponseEntity<TransacaoResponse> realizarTransferencia(
            @Valid @RequestBody TransacaoRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarTransferencia(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/deposito")
    public ResponseEntity<TransacaoResponse> realizarDeposito(
            @Valid @RequestBody TransacaoRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarDeposito(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/saque")
    public ResponseEntity<TransacaoResponse> realizarSaque(
            @Valid @RequestBody TransacaoRequest request,
            @RequestParam Long gerenteExecutorId) {
        TransacaoResponse response = transacaoService.realizarSaque(request, gerenteExecutorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/extrato/{contaId}")
    public ResponseEntity<List<TransacaoResponse>> buscarExtratoPorConta(@PathVariable Long contaId) {
        List<TransacaoResponse> extrato = transacaoService.buscarExtratoPorConta(contaId);
        return ResponseEntity.ok(extrato);
    }
}

