package com.totaldocs.controle;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.annotation.CheckSession;
import com.totaldocs.dto.ChecklistPaginadoResponse;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.service.ChecklistVersaoService;
import com.totaldocs.service.ChecklistVersaoServiceAPI;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController extends AbstractController {

    @Autowired
    private ChecklistVersaoServiceAPI checkVersaoServiceAPI;
    
    @Autowired
    private ChecklistVersaoService checklistVersaoService;
    
    @Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;

    @GetMapping("/list")
    @CheckSession
    public ChecklistPaginadoResponse listar(
    		HttpSession session,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        boolean isAdmin = getIsAdminSession(session);
        Integer idUser = getUserIdSession(session);
        Page<ChecklistVersaoDTO> pagina =
                checkVersaoServiceAPI.listarPaginadoDTO(pageable,isAdmin,idUser);

        return new ChecklistPaginadoResponse(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getTotalPages(),
                pagina.getTotalElements()
        );
    }
    
    @GetMapping("/user/")
    @CheckSession
    public ResponseEntity<?> getUser(HttpSession session)
    {
    	return getUserLogged(session);
    }
    
    @GetMapping("/{idChecklistVersaoStr}/export")
    @CheckSession
    public ResponseEntity<byte[]> exportChecklist(
            HttpSession session,
            @PathVariable String idChecklistVersaoStr,
            @RequestParam(defaultValue = "zip") String format
    ) throws IOException {

       

        Integer idChecklistVersao = temporalCryptoIdUtil.extractId(idChecklistVersaoStr);
        ChecklistVersaoDTO checklist =
                checkVersaoServiceAPI.getChecklistVersaoDTOById(idChecklistVersao);

        if (!"zip".equalsIgnoreCase(format)) {
            throw new IllegalArgumentException("Formato inválido");
        }

        byte[] zipBytes = checklistVersaoService.generateZipFromCheckList(checklist);
        
        String zipName = checklistVersaoService.nameFolderOrZip(checklist,true);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + zipName + ".zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipBytes.length)
                .body(zipBytes);
    }
}
