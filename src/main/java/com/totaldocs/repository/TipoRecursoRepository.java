package com.totaldocs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.TipoRecurso;

@Repository
public interface TipoRecursoRepository extends JpaRepository<TipoRecurso, Integer> {
    Optional<TipoRecurso> findByCodigo(Integer codigo);
}
