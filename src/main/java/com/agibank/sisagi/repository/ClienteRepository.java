package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    //TODO: Implementar security para remover aviso
    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByCpf(String cpf);

    Optional<Cliente> findByGerenteId(Long gerenteId);
}
