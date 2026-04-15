package com.totaldocs.repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
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
		    SELECT cv 
		    FROM ChecklistVersao cv
		    WHERE cv.idChecklistVersao = (
		        SELECT MAX(cv2.idChecklistVersao)
		        FROM ChecklistVersao cv2
		        WHERE cv2.checklist.id = cv.checklist.id
		    )
		""")
		Page<ChecklistVersao> findUltimasVersoes(Pageable pageable);
	
	
	List<ChecklistVersao> findByChecklistIdOrderByVersaoDesc(Integer idChecklist);
	
	@Query("""
		    SELECT cv 
		    FROM ChecklistVersao cv
		    WHERE cv.idChecklistVersao = (
		        SELECT MAX(cv2.idChecklistVersao)
		        FROM ChecklistVersao cv2
		        WHERE cv2.checklist.id = cv.checklist.id
		        AND cv2.usuario.id = :idUser
		    )
		    AND cv.usuario.id = :idUser
		    ORDER BY cv.dataAtualizacao DESC
		""")
		Page<ChecklistVersao> findByUsuarioIdUltimasVersoes(
		    @Param("idUser") Integer idUser,
		    Pageable pageable
		);
}
