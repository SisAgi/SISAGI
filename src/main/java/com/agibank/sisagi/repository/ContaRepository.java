package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    Optional<Conta> findByNumeroConta(String numeroConta);

    Optional<Conta> findById(Long contaId);

    @Query("SELECT c FROM Conta c JOIN c.titulares t WHERE t.cpf = :cpf")
    List<Conta> findByTitularCpf(@Param("cpf") String cpf);
}
