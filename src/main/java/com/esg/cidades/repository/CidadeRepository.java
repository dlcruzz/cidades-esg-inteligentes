package com.esg.cidades.repository;

import com.esg.cidades.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    List<Cidade> findByUfIgnoreCase(String uf);

    List<Cidade> findByNomeContainingIgnoreCase(String nome);
}
