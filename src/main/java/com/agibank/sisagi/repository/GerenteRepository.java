package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    Optional<Gerente> findByMatricula(String matricula);

    Optional<Gerente> findByEmail(String email);
}
