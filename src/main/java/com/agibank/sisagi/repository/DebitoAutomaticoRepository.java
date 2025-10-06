package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.DebitoAutomatico;
import com.agibank.sisagi.model.enums.StatusDebito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DebitoAutomaticoRepository extends JpaRepository<DebitoAutomatico, Long> {
    DebitoAutomatico findByNumeroReferencia(String numeroReferencia);

    List<DebitoAutomatico> findByStatus(StatusDebito status);

    List<DebitoAutomatico> findByDataDebitoBetween(LocalDate dataInicio, LocalDate dataFim);
}
