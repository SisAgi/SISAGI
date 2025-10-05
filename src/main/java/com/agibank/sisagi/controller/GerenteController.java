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

    // CREATE
    @PostMapping
    public ResponseEntity<GerenteResponse> criarGerente(@RequestBody GerenteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gerenteService.criarGerente(request));
    }

    // READ (ALL)
    @GetMapping
    public ResponseEntity<List<GerenteResponse>> getAllGerentes() {
        return ResponseEntity.ok(gerenteService.listarTodos());
    }

    // READ (ONE)
    @GetMapping("/{id}")
    public ResponseEntity<GerenteResponse> getGerenteById(@PathVariable Long id) {
        return ResponseEntity.ok(gerenteService.buscarPorId(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<GerenteResponse> updateGerente(@PathVariable Long id, @RequestBody GerenteRequest request) {
        return ResponseEntity.ok(gerenteService.atualizarGerente(id, request));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGerente(@PathVariable Long id) {
        gerenteService.deletarGerente(id);
        return ResponseEntity.noContent().build();
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {

        if (request.gerenteId().equals(1L) && "123456".equals(request.senha())) {
            return ResponseEntity.ok("Login bem-sucedido");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
    }



}