package com.totaldocs.controle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.modelo.MassaDados;
import com.totaldocs.repository.MassaDadoRepository;

@RestController
@RequestMapping("/api/massas")
public class MassaDadosController {
	@Autowired
    private MassaDadoRepository massaDadoRepository;
	
	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> downloadMassa(@PathVariable Integer id) {
	
	        MassaDados massaDados = massaDadoRepository.findById(id)
	            .orElseThrow(() ->
	                new RuntimeException("Layout não encontrado id " + id)
	            );

	        return ResponseEntity.ok()
	            .header(
	                HttpHeaders.CONTENT_DISPOSITION,
	                "attachment; filename=\"" + massaDados.getNomeMassaDados() + "\""
	            )
	            .contentType(MediaType.parseMediaType(massaDados.getTipoMIME()))
	            .body(massaDados.getConteudoMassaDados());
	}
	    
}
