package com.totaldocs.controle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.ChecklistVersaoResumoDTO;
import com.totaldocs.modelo.*;
import com.totaldocs.service.ChecklistService;
import com.totaldocs.service.ChecklistVersaoServiceAPI;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/Checklists")
public class ChecklistVersaoControleAPI extends AbstractController {

	private final ChecklistVersaoServiceAPI checklistVersaoServiceAPI;
//	private final ChecklistVersaoServiceAPI checklistService;              

	public ChecklistVersaoControleAPI(ChecklistVersaoServiceAPI serviceVersao) {
		this.checklistVersaoServiceAPI = serviceVersao;
	}

	@PostMapping(value = "/salvar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> criar(
	        @RequestPart("dados") String dadosJson,
	        @RequestPart(value = "filesLayout", required = false) List<MultipartFile> arquivosLayout,
	        @RequestPart(value = "filesMassas", required = false) List<MultipartFile> arquivosMassas, HttpSession session) {
		getUserFromSession(session);
	    try {
	    	ObjectMapper mapper = new ObjectMapper();

	        // Agora convertemos JSON corretamente → ChecklistDTO
	        ChecklistVersaoDTO dto = mapper.readValue(dadosJson, ChecklistVersaoDTO.class);
	        
	        ChecklistVersaoDTO salvo = checklistVersaoServiceAPI.criar(dto, arquivosLayout, arquivosMassas);

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
	
	@PostMapping("/{idChecklist}/editar")
	public ChecklistVersaoDTO editar(@PathVariable String idChecklist,
								     @RequestPart("dto") ChecklistVersaoDTO dto,
								     @RequestPart(value = "filesLayout", required = false ) List<MultipartFile> filesLayout,
								     @RequestPart(value = "filesMassas", required = false ) List<MultipartFile> filesMassas) throws IOException {
	    return checklistVersaoServiceAPI.salvarVersao(idChecklist, dto, filesLayout, filesMassas);
	}
		
	@GetMapping("/page")
	public Page<ChecklistVersaoDTO> getDocumentos(Pageable pageable) {
		return checklistVersaoServiceAPI.listarPaginadoDTO(pageable);
	}
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;


	@GetMapping("/{idStr}")
	public ChecklistVersaoDTO getDocumentoDTOById(@PathVariable String idStr) {
		Integer id = temporalCryptoIdUtil.extractId(idStr);
		return checklistVersaoServiceAPI.getChecklistVersaoDTOById(id);
	}
	
	@GetMapping("/{idChecklistStr}/versoes")
	public List<ChecklistVersaoResumoDTO> listarVersoes(@PathVariable String idChecklistStr) {
		Integer idChecklist = temporalCryptoIdUtil.extractId(idChecklistStr);
	    return checklistVersaoServiceAPI.listarVersoesChecklist(idChecklist);
	}
}