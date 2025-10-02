package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.ClienteRequest;
import com.agibank.sisagi.dto.ClienteResponse;
import com.agibank.sisagi.dto.ClienteUpdateRequest;
import com.agibank.sisagi.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAllClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@Valid @PathVariable Long id, @RequestBody ClienteUpdateRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
