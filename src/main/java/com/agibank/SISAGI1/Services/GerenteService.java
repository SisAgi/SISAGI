package com.agibank.SISAGI1.Services;

import com.agibank.SISAGI1.DTOs.GerenteRequest;
import com.agibank.SISAGI1.DTOs.GerenteResponse;
import com.agibank.SISAGI1.Entities.Gerente;
import com.agibank.SISAGI1.Repositories.GerenteRepository;
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
        novoGerente.setNome(request.nome());
        novoGerente.setEmail(request.email());
        novoGerente.setSenha(request.senha());
        novoGerente.setMatricula(request.matricula());

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
                .orElseThrow(() -> new IllegalArgumentException ("Gerente não encontrado com o ID: " + id));

        gerenteExistente.setNome(request.nome());
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
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getMatricula());
    }
}

