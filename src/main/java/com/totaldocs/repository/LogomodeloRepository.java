package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.Logomodelo;

@Repository
public interface LogomodeloRepository extends JpaRepository<Logomodelo, Integer> {}
