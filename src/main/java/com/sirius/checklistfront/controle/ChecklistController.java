package com.sirius.checklistfront.controle;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.modelo.Ramo;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.service.ChecklistServiceAPI;
import com.totaldocs.service.RamoService;
import com.totaldocs.service.UsuarioService;

@Controller
public class ChecklistController {
	@Value("${backend.api.base-url}")
	private String API_URL;
	
	@Autowired
    private ChecklistServiceAPI checklistService;

	@Autowired
	private RamoService ramoService;
	
	@Autowired UsuarioService usuarioService;
	
    @GetMapping({"/", "/index"})
    public String index(Model model, Authentication authentication) {
    	String login = authentication.getName();
    	
    	Optional<Usuario> usuarioLogado = usuarioService.getUsuario(login);
    	
    	// busca os ramos via API	
        List<Ramo> listaRamos = ramoService.ListarTodos();
        
        Pageable page = PageRequest.of(0, 20);
        Page<ChecklistDTO> pagina = checklistService.listarPaginadoDTO(page);
        
        model.addAttribute("usuarioLogado",usuarioLogado.get());
        model.addAttribute("checklists", pagina.getContent());
        model.addAttribute("ramos", listaRamos);
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", pagina.getTotalPages());
        model.addAttribute("apiUrl", API_URL);
        return "index";
    }   
}
