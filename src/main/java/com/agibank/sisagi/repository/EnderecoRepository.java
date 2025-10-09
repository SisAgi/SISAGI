package com.agibank.sisagi.repository;

import com.agibank.sisagi.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    List<Endereco> findByClienteId(Long clienteId);

    Optional<Endereco> findByClienteIdAndTipoEndereco(Long clienteId, String tipoEndereco);

    List<Endereco> findByCep(String cep);

    List<Endereco> findByCidadeAndEstado(String cidade, String estado);
}
