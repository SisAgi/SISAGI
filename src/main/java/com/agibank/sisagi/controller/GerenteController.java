package com.agibank.sisagi.controller;

import com.agibank.sisagi.dto.GerenteRequest;
import com.agibank.sisagi.dto.GerenteResponse;
import com.agibank.sisagi.dto.LoginRequest;
import com.agibank.sisagi.service.GerenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gerentes")
@RequiredArgsConstructor
public class GerenteController {

    private final GerenteService gerenteService;

    // Endpoint para a criação de um novo gerente
    @PostMapping
    public ResponseEntity<GerenteResponse> criarGerente(@RequestBody GerenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gerenteService.criarGerente(request));
    }

    // Endpoint para listar todos os gerentes
    @GetMapping
    public ResponseEntity<List<GerenteResponse>> getAllGerentes() {
        return ResponseEntity.ok(gerenteService.listarTodos());
    }

    // Endpoint para buscar um gerente pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<GerenteResponse> getGerenteById(@PathVariable Long id) {
        return ResponseEntity.ok(gerenteService.buscarPorId(id));
    }

    // Endpoint para atualizar os dados de um gerente existente
    @PutMapping("/{id}")
    public ResponseEntity<GerenteResponse> updateGerente(@PathVariable Long id, @RequestBody GerenteRequest request) {
        return ResponseEntity.ok(gerenteService.atualizarGerente(id, request));
    }

    // Endpoint para deletar um gerente pelo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGerente(@PathVariable Long id) {
        gerenteService.deletarGerente(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint para login do gerente
    @PostMapping("/login")
    public ResponseEntity<GerenteResponse> login(@RequestBody LoginRequest request) {
        try {
            GerenteResponse response = gerenteService.validarLogin(request.gerenteId(), request.senha());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }




}