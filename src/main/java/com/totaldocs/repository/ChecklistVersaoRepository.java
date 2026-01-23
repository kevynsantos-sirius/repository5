package com.totaldocs.repository;

import java.awt.print.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.totaldocs.modelo.ChecklistVersao;

public interface ChecklistVersaoRepository extends JpaRepository<ChecklistVersao, Integer>{
	@Query("""
	        select max(v.versao)
	        from ChecklistVersao v
	        where v.checklist.id = :idChecklist
	    """)
	Optional<Integer> findMaxVersaoByChecklistId(@Param("idChecklist") Integer idChecklist);
	
	@Query("""
		    SELECT v
		    FROM ChecklistVersao v
		    WHERE v.versao = (
		        SELECT MAX(v2.versao)
		        FROM ChecklistVersao v2
		        WHERE v2.checklist.id = v.checklist.id
		    )
		""")
		Page<ChecklistVersao> findUltimasVersoes(org.springframework.data.domain.Pageable pageable);
	
}
