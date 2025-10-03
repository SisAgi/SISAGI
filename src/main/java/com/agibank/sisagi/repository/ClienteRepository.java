package com.agibank.sisagi.repository;

import com.agibank.sisagi.dto.ResponsavelTitularDTO;
import com.agibank.sisagi.model.Cliente;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findById(Long id);
    Optional<Cliente> findByGerenteId(Long gerenteId);

    Cliente findById(@NotNull(message = "O campo de titular responsavel é obrigatório") ResponsavelTitularDTO responsavelTitularDTO);

}
