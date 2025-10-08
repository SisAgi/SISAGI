package com.agibank.sisagi.service;

import com.agibank.sisagi.dto.GerenteRequest;
import com.agibank.sisagi.dto.GerenteResponse;
import com.agibank.sisagi.model.Gerente;
import com.agibank.sisagi.model.enums.UserRole;
import com.agibank.sisagi.repository.GerenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GerenteService {
    private final GerenteRepository gerenteRepository;

    // CREATE
    @Transactional
    public GerenteResponse criarGerente(GerenteRequest request) {
        if (gerenteRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        if (gerenteRepository.findByMatricula(request.matricula()).isPresent()) {
            throw new IllegalArgumentException("Matrícula funcional já cadastrada.");
        }

        Gerente novoGerente = new Gerente();
        novoGerente.setNomeCompleto(request.nome());
        novoGerente.setEmail(request.email());
        novoGerente.setSenha(request.senha());
        novoGerente.setMatricula(request.matricula());
        novoGerente.setRole(UserRole.GERENTE);

        Gerente gerenteSalvo = gerenteRepository.save(novoGerente);
        return mapToResponse(gerenteSalvo);
    }

    // READ (Single)
    @Transactional(readOnly = true)
    public GerenteResponse buscarPorId(Long id) {
        Gerente gerente = gerenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado com o ID: " + id));
        return mapToResponse(gerente);
    }

    // READ (All)
    @Transactional(readOnly = true)
    public List<GerenteResponse> listarTodos() {
        return gerenteRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    @Transactional
    public GerenteResponse atualizarGerente(Long id, GerenteRequest request) {
        Gerente gerenteExistente = gerenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gerente não encontrado com o ID: " + id));
        gerenteExistente.setNomeCompleto(request.nome());
        gerenteExistente.setEmail(request.email());
        gerenteExistente.setMatricula(request.matricula());
        Gerente gerenteAtualizado = gerenteRepository.save(gerenteExistente);
        return mapToResponse(gerenteAtualizado);
    }

    // DELETE
    @Transactional
    public void deletarGerente(Long id) {
        if (!gerenteRepository.existsById(id)) {
            throw new IllegalArgumentException("Gerente não encontrado com o ID: " + id);
        }
        gerenteRepository.deleteById(id);
    }

    private GerenteResponse mapToResponse(Gerente gerente) {
        return new GerenteResponse(
                gerente.getId(),
                gerente.getNomeCompleto(),
                gerente.getEmail(),
                gerente.getMatricula());
    }
}

