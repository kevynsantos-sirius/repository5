package com.totaldocs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.totaldocs.modelo.Ramo;

@Repository
public interface RamoRepository extends JpaRepository<Ramo, Integer>{

}
