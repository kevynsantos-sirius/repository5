package com.totaldocs.controle;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.modelo.LogoCapa;
import com.totaldocs.modelo.Ramo;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.service.ChecklistServiceAPI;
import com.totaldocs.service.LogoCapaService;
import com.totaldocs.service.RamoService;
import com.totaldocs.service.UsuarioService;

@Controller
public class ChecklistController {
	@Value("${backend.api.base-url}")
	private String API_URL;
	
	@Autowired
    private ChecklistServiceAPI checklistServiceAPI;

	@Autowired
	private RamoService ramoService;
	
	@Autowired UsuarioService usuarioService;
	
	@Autowired LogoCapaService logoCapaService;
	
    @GetMapping({"/", "/index"})
    public String index(@RequestParam(name = "page", defaultValue = "0") int page, 
    		            Model model, 
    		            Authentication authentication) {
    	
    	String login = authentication.getName();
    	
    	Optional<Usuario> usuarioLogado = usuarioService.getUsuario(login);
    	
    	// busca os ramos via API	
        List<Ramo> listaRamos = ramoService.ListarTodos();
        
        List<LogoCapa> listaLogoCapas = logoCapaService.ListarTodos();
        
        int size = 20; // ou 20, como preferir

        Pageable pageable = PageRequest.of(page, size);
        Page<ChecklistDTO> pagina = checklistServiceAPI.listarPaginadoDTO(pageable);
        
        model.addAttribute("listaLogoCapas", listaLogoCapas);
        model.addAttribute("usuarioLogado",usuarioLogado.get());
        model.addAttribute("checklists", pagina.getContent());
        model.addAttribute("ramos", listaRamos);
        model.addAttribute("paginaAtual", pagina.getNumber());
        model.addAttribute("totalPaginas", pagina.getTotalPages());
        model.addAttribute("apiUrl", API_URL);
        return "index";
    }   
}
