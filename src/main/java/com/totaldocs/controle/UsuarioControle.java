package com.totaldocs.controle;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.modelo.Usuario;
import com.totaldocs.service.UsuarioService;

@RestController
@RequestMapping("/api")
public class UsuarioControle {
	private final UsuarioService usuarioService;
	
	public UsuarioControle(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	
	public Optional<Usuario> getUsuario(Integer idUsuario) {
		return usuarioService.getUsuario(idUsuario);
	}
	
	
	
	@GetMapping("/usuarios")
	public List<Usuario>listarTodos(){
		return usuarioService.ListarTodos();
	}
	
	
}
