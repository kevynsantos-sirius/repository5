package com.totaldocs.controle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.modelo.*;
import com.totaldocs.service.ChecklistServiceAPI;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/Checklists")
public class ChecklistControleAPI {

	private final ChecklistServiceAPI checklistServiceAPI;

	public ChecklistControleAPI(ChecklistServiceAPI service) {
		this.checklistServiceAPI = service;
	}

	@PostMapping(value = "/salvar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> criar(
	        @RequestPart("dados") String dadosJson,
	        @RequestPart(value = "filesLayout", required = false) List<MultipartFile> arquivosLayout,
	        @RequestPart(value = "filesMassas", required = false) List<MultipartFile> arquivosMassas) {

	    try {
	    	ObjectMapper mapper = new ObjectMapper();

	        // Agora convertemos JSON corretamente → ChecklistDTO
	        ChecklistDTO dto = mapper.readValue(dadosJson, ChecklistDTO.class);
	        
	        ChecklistDTO salvo = checklistServiceAPI.criar(dto, arquivosLayout, arquivosMassas);

	        return ResponseEntity
	                .status(HttpStatus.CREATED)
	                .body(salvo);

	    } catch (Exception e) {
	        e.printStackTrace(); // log no servidor
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("Erro ao salvar checklist", e.getMessage(), 0));
	    }
	}
	
	@GetMapping("/page")
	public Page<ChecklistDTO> getDocumentos(Pageable pageable) {
		return checklistServiceAPI.listarPaginadoDTO(pageable);
	}

	/*
	 * @GetMapping("/{id}") public Checklist getDocumentoById(@PathVariable Integer
	 * id) { return checklistServiceAPI.getDocumentoById(id); }
	 */
	
	@GetMapping("/{id}")
	public ChecklistDTO getDocumentoDTOById(@PathVariable Integer id) {
		return checklistServiceAPI.getChecklistDTOById(id);
	}
}