package com.agibank.sisagi.repository;

import com.agibank.sisagi.dto.dashboard.ContasPorGerenteResponse;
import com.agibank.sisagi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByGerenteId(Long gerenteId);

    @Query("SELECT new com.agibank.sisagi.dashboard.dto.ContasPorGerenteResponse(c.gerente.nome, COUNT(c)) FROM Cliente c WHERE c.gerente IS NOT NULL GROUP BY c.gerente.nome ORDER BY COUNT(c) DESC")
    List<ContasPorGerenteResponse> countClientesByGerente();
}
