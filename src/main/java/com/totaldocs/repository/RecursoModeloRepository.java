package com.totaldocs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.totaldocs.modelo.RecursoModelo;

public interface RecursoModeloRepository extends JpaRepository<RecursoModelo,Long> {
	
	
	List<RecursoModelo> findByChecklistVersaoIdChecklistVersao(Integer id);

}
