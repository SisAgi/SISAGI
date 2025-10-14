package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentosRepository  extends JpaRepository<Documento, Long> {
}
