package com.totaldocs.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.LogomodeloTipo;

@Repository
public interface LogomodeloTipoRepository extends JpaRepository<LogomodeloTipo, Integer> {
    Optional<LogomodeloTipo> findByCodigo(Integer codigo);
}
