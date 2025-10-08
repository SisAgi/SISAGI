package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.*;
import com.agibank.sisagi.model.ContaCorrente;
import com.agibank.sisagi.model.ContaPoupanca;
import com.agibank.sisagi.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {
    private final ContaService contaService;

    @PostMapping("/corrente")
    public ResponseEntity<ContaCorrenteResponse> criarContaCorrente(@Valid @RequestBody ContaCorrenteRequest contaCorrente) {
        return ResponseEntity.ok(contaService.criarContaCorrente(contaCorrente));
    }
    @PostMapping("/poupanca")
    public ResponseEntity<ContaPoupResponse> criarContaPoupanca(@Valid @RequestBody ContaPoupRequest contaPoupanca) {
        return ResponseEntity.ok(contaService.criarContaPoupanca(contaPoupanca));
    }
    @PostMapping("/contajovem")
    public ResponseEntity<ContaJovemResponse> criarContaJovem(@Valid @RequestBody ContaJovemRequest contaJovemResponse){
        return ResponseEntity.ok(contaService.criarContaJovem(contaJovemResponse));
    }
    @GetMapping("/{contaId}")
    public ResponseEntity<Object> getContaById(@PathVariable Long contaId) {
        Object contaResponse = contaService.buscarDetalhesConta(contaId);
        return ResponseEntity.ok(contaResponse);
    }

}
