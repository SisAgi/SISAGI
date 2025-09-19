package com.agibank.SISAGI1.Cliente.Repository;

import com.agibank.SISAGI1.Cliente.Entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
   Optional<Cliente> findByGerenteId(Long gerenteId);
}
