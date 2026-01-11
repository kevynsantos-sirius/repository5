package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.LogoCapa;

@Repository
public interface LogoCapaRepository extends JpaRepository<LogoCapa, Integer>{
}
