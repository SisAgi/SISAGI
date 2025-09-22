package com.agibank.SISAGI1.Repositories;

import com.agibank.SISAGI1.Entities.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    Optional<Gerente> findByMatricula(String matricula);

    Optional<Gerente> findByEmail(String email);
}
