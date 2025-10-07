package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.DebitoAutomaticoRequest;
import com.agibank.sisagi.dto.DebitoAutomaticoResponse;
import com.agibank.sisagi.service.DebitoAutomaticoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debitos-automaticos")
public class DebitoAutomaticoController {

    private final DebitoAutomaticoService debitoService;

    @Autowired
    public DebitoAutomaticoController(DebitoAutomaticoService debitoService) {
        this.debitoService = debitoService;
    }

    @GetMapping
    public ResponseEntity<List<DebitoAutomaticoResponse>> listarDebitos() {
        List<DebitoAutomaticoResponse> response = debitoService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebitoAutomaticoResponse> buscarDebito(@PathVariable Long id) {
        DebitoAutomaticoResponse response = debitoService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DebitoAutomaticoResponse> criarDebito(@Valid @RequestBody DebitoAutomaticoRequest request) {
        DebitoAutomaticoResponse response = debitoService.criarDebito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/cancelar/{id}")
    public ResponseEntity<DebitoAutomaticoResponse> cancelarDebito(@PathVariable Long id) {
        DebitoAutomaticoResponse response = debitoService.cancelarDebito(id);
        return ResponseEntity.ok(response);
    }
}
