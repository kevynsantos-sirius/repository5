package com.totaldocs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.totaldocs.modelo.RecursoPlanoComunicacao;

public interface RecursoPlanoComunicacaoRepository extends JpaRepository<RecursoPlanoComunicacao, Long> {

    List<RecursoPlanoComunicacao> findByChecklistVersaoIdChecklistVersao(Long idChecklistVersao);

}