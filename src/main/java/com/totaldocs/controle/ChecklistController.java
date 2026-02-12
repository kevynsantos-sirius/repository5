package com.totaldocs.controle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.dto.ChecklistPaginadoResponse;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.service.ChecklistVersaoServiceAPI;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController extends AbstractController {

    @Autowired
    private ChecklistVersaoServiceAPI checkVersaoServiceAPI;

    @GetMapping("/list")
    public ChecklistPaginadoResponse listar(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChecklistVersaoDTO> pagina =
                checkVersaoServiceAPI.listarPaginadoDTO(pageable);

        return new ChecklistPaginadoResponse(
                pagina.getContent(),
                pagina.getNumber(),
                pagina.getTotalPages(),
                pagina.getTotalElements()
        );
    }
    
    @GetMapping("/user/name")
    public String userName(HttpSession session)
    {
    	return getUsernameUserLogged(session);
    }
}
