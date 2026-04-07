package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.Recurso;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Integer> {}
