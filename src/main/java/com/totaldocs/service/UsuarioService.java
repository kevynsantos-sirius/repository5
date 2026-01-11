package com.totaldocs.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.UsuarioRepository;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	public Optional<Usuario> getUsuario(Integer idUsuario) {
		return usuarioRepository.findById(idUsuario);
	}
	
	public Optional<Usuario> getUsuario(String login) {
		return usuarioRepository.findByLogin(login);
	}
	public List<Usuario> ListarTodos(){
		return usuarioRepository.findAll();
	}
}
