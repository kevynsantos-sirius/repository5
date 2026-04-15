package com.totaldocs.controle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totaldocs.annotation.CheckSession;
import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.ChecklistVersaoResumoDTO;
import com.totaldocs.modelo.*;
import com.totaldocs.service.ChecklistService;
import com.totaldocs.service.ChecklistVersaoServiceAPI;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Map<String, MultipartFile> collectModels(
	        List<MultipartFile> arquivosModelos,
	        List<String> keysModelos) {

	    Map<String, MultipartFile> mapa = new HashMap<>();

	    if (arquivosModelos == null || keysModelos == null) {
	        return mapa;
	    }

	    int fileIndex = 0;

	    for (int i = 0; i < keysModelos.size(); i++) {
	        String key = keysModelos.get(i);

	        if (key == null || key.trim().isEmpty()) {
	            continue;
	        }

	        // Só pega File quando existir File
	        if (fileIndex < arquivosModelos.size()) {
	            mapa.put(key, arquivosModelos.get(fileIndex));
	            fileIndex++; // avança só na lista de FILES novos
	        }
	    }

	    return mapa;
	}

	@PostMapping(value = "/salvar")
	@CheckSession
	public ResponseEntity<?> criar(
	        @RequestPart(value = "dados", required = false) String dadosJson,
	        @RequestPart(value = "filesLayout", required = false) List<MultipartFile> arquivosLayout,
	        @RequestPart(value = "filesMassas", required = false) List<MultipartFile> arquivosMassas,
	        @RequestPart(value = "arquivosModelos", required = false) List<MultipartFile> arquivosModelos,
	        @RequestPart(value = "arquivosPlano", required = false) List<MultipartFile> arquivosPlano,
	        @RequestPart(value = "keysModelos", required = false) List<String> keysModelos, // ⚡ map now
	        @RequestPart(value = "keysPlanos", required = false) List<String> keysPlanos,
	        HttpSession session) {

	    try {
	        ObjectMapper mapper = new ObjectMapper();

	        ChecklistVersaoDTO dto = mapper.readValue(dadosJson, ChecklistVersaoDTO.class);
	        
	        Map arquivosModelosNew = collectModels(arquivosModelos, keysModelos);
	        
	        Map arquivosPlanoNew = collectModels(arquivosPlano, keysPlanos);
	        
	        System.out.print(arquivosPlanoNew);

	        ChecklistVersaoDTO salvo = checklistVersaoServiceAPI.criar(dto, arquivosLayout, arquivosMassas, arquivosModelosNew, arquivosPlanoNew);

	        return ResponseEntity
	                .status(HttpStatus.CREATED)
	                .body(salvo);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ErrorResponse("Erro ao salvar checklist", e.getMessage(), 0));
	    }
	}
	
	@PostMapping(value = "/{idChecklist}/editar")
	@CheckSession
	public ChecklistVersaoDTO editar(@PathVariable String idChecklist,
	     @RequestPart(value = "dto", required = false) String dtoJson,
	     @RequestPart(value = "filesLayout", required = false) List<MultipartFile> filesLayout,
	     @RequestPart(value = "filesMassas", required = false) List<MultipartFile> filesMassas,
	     @RequestPart(value = "arquivosModelos", required = false) List<MultipartFile> arquivosModelos,
	     @RequestPart(value = "arquivosPlano", required = false) List<MultipartFile> arquivosPlano,
	     @RequestPart(value = "keysModelos", required = false) List<String> keysModelos,
	     @RequestPart(value = "keysPlanos", required = false) List<String> keysPlanos) throws Exception {

	     ObjectMapper mapper = new ObjectMapper();
	     ChecklistVersaoDTO dto = mapper.readValue(dtoJson, ChecklistVersaoDTO.class);

	     Map arquivosModelosNew = collectModels(arquivosModelos, keysModelos);
	     
	     Map arquivosPlanoNew = collectModels(arquivosPlano, keysPlanos);
	     
	     System.out.print(arquivosPlanoNew);

	     return checklistVersaoServiceAPI.salvarVersao(idChecklist, dto, filesLayout, filesMassas, arquivosModelosNew, arquivosPlanoNew);
	}
		
	@GetMapping("/page")
	@CheckSession
	public Page<ChecklistVersaoDTO> getDocumentos(Pageable pageable, HttpSession session) {
		boolean isAdmin = getIsAdminSession(session);
        Integer idUser = getUserIdSession(session);
		return checklistVersaoServiceAPI.listarPaginadoDTO(pageable,isAdmin,idUser);
	}
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;


	@GetMapping("/{idStr}")
	@CheckSession
	public ChecklistVersaoDTO getDocumentoDTOById(@PathVariable String idStr) {
		Integer id = temporalCryptoIdUtil.extractId(idStr);
		return checklistVersaoServiceAPI.getChecklistVersaoDTOById(id);
	}
	
	@GetMapping("/{idChecklistStr}/versoes")
	@CheckSession
	public List<ChecklistVersaoResumoDTO> listarVersoes(@PathVariable String idChecklistStr) {
		Integer idChecklist = temporalCryptoIdUtil.extractId(idChecklistStr);
	    return checklistVersaoServiceAPI.listarVersoesChecklist(idChecklist);
	}
}