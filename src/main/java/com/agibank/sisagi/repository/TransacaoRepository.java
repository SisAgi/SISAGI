package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Conta;
import com.agibank.sisagi.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    Transacao findByNsUnico(String nsUnico);

    List<Transacao> findByConta(Conta conta);
}