package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.PlanoComunicacao;

@Repository
public interface PlanoComunicacaoRepository extends JpaRepository<PlanoComunicacao,Long> {

}
