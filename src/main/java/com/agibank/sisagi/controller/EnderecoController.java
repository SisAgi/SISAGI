package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.EnderecoRequest;
import com.agibank.sisagi.dto.EnderecoResponse;
import com.agibank.sisagi.dto.EnderecoUpdateRequest;
import com.agibank.sisagi.dto.ViaCepResponse;
import com.agibank.sisagi.service.EnderecoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enderecos")
@RequiredArgsConstructor
public class EnderecoController {

    private final EnderecoService enderecoService;

    @PostMapping
    public ResponseEntity<EnderecoResponse> criarEndereco(@RequestBody EnderecoRequest request) {
        return ResponseEntity.ok(enderecoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<EnderecoResponse>> listarTodosEnderecos() {
        return ResponseEntity.ok(enderecoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoResponse> buscarEnderecoPorId(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.buscarPorId(id));
    }

    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponse> buscarEnderecoPorCep(@PathVariable String cep) {
        return ResponseEntity.ok(enderecoService.buscarEnderecoPorCep(cep));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EnderecoResponse>> listarEnderecosPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(enderecoService.listarPorCliente(clienteId));
    }

    @GetMapping("/cliente/{clienteId}/tipo/{tipoEndereco}")
    public ResponseEntity<EnderecoResponse> buscarEnderecoPorClienteETipo(
            @PathVariable Long clienteId, 
            @PathVariable String tipoEndereco) {
        return ResponseEntity.ok(enderecoService.buscarPorClienteETipo(clienteId, tipoEndereco));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoResponse> atualizarEndereco(
            @PathVariable Long id, 
            @RequestBody EnderecoUpdateRequest request) {
        return ResponseEntity.ok(enderecoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEndereco(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
