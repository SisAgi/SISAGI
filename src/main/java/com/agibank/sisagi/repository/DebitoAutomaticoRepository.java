package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.DebitoAutomatico;
import com.agibank.sisagi.model.enums.StatusDebito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebitoAutomaticoRepository extends JpaRepository<DebitoAutomatico, Long> {

    DebitoAutomatico findByIdentificadorConvenio(String identificadorConvenio);

    List<DebitoAutomatico> findByStatus(StatusDebito status);
}
