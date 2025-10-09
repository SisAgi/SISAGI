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

    // Endpoint para a criação de um novo cliente
    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(clienteService.criar(request));
    }

    // Endpoint para listar todos os clientes
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> getAllClientes() {
        return ResponseEntity.ok(clienteService.listarTodos());
    }

    // Endpoint para buscar um cliente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> getClienteById(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // Endpoint para atualizar os dados de um cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> updateCliente(@Valid @PathVariable Long id, @RequestBody ClienteUpdateRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    // Endpoint para deletar um cliente pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
