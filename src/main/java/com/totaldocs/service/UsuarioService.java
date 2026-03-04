package com.totaldocs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.totaldocs.exception.UserNotExists;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.UsuarioRepository;
import com.totaldocs.utils.TemporalCryptoIdUtil;

@Service
public class UsuarioService {
	private final UsuarioRepository usuarioRepository;
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;
	
	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	public Optional<Usuario> getUsuario(Integer idUsuario) {
		return usuarioRepository.findById(idUsuario);
	}
	
	public Optional<Usuario> getUsuario(String login) {
		return usuarioRepository.findByLogin(login);
	}

	public List<com.totaldocs.dto.Usuario> listarTodos() {

	    return usuarioRepository.findAll()
	            .stream()
	            .map(usuario -> {
	            	com.totaldocs.dto.Usuario dto = new com.totaldocs.dto.Usuario();
	                dto.setId(temporalCryptoIdUtil.generateToken(usuario.getId()));
	                dto.setLogin(usuario.getLogin());
	                dto.setNome(usuario.getNome());
	                dto.setBloqueado(usuario.isFlgBloqueado());
	                dto.setPerfis("S".equals(usuario.getEhAdmin()) ? List.of("ADMIN") : List.of("USER"));
	                dto.setEmail(usuario.getEmail());
	                return dto;
	            })
	            .toList();
	}

	@Transactional
	public com.totaldocs.dto.Usuario criarUsuario(com.totaldocs.dto.Usuario usuario) {
		// TODO Auto-generated method stub
		Usuario user = new Usuario();
		user = putDataUser(usuario, user);
		user = usuarioRepository.saveAndFlush(user);
		usuario.setId(temporalCryptoIdUtil.generateToken(user.getId()));
		return usuario;
	}

	private Usuario putDataUser(com.totaldocs.dto.Usuario usuario, Usuario user) {
		user.setEhAdmin("N");
		user.setEmail(usuario.getEmail());
		user.setFlgBloqueado(usuario.isBloqueado());
		user.setExcluido(usuario.isExcluido() ? "S" : "N");
		user.setLogin(usuario.getLogin());
		user.setNome(usuario.getNome());
		user.setSenha(usuario.getSenha());
		user.setSetor("Desenvolvimento");
		user.setRecLog("");
		user.setPermissoes("");
		
		return user;
	}
	
	@Transactional
	public com.totaldocs.dto.Usuario atualizarUsuario(String idStr, com.totaldocs.dto.Usuario usuario) throws UserNotExists {
		// TODO Auto-generated method stub
		Integer id = temporalCryptoIdUtil.extractId(idStr);
		Optional<Usuario> user = usuarioRepository.findById(id);
		if(user.isPresent())
		{
			Usuario u = user.get();
			
			u = putDataUser(usuario, u);
			
			u = usuarioRepository.saveAndFlush(u);
			
			usuario.setId(temporalCryptoIdUtil.generateToken(u.getId()));
			
			return usuario;
		}
		else
		{
			throw new UserNotExists();
		}
	}

	@Transactional
	public void deletarUsuario(String idStr) throws UserNotExists {
		// TODO Auto-generated method stub
		Integer id = temporalCryptoIdUtil.extractId(idStr);
		Optional<Usuario> user = usuarioRepository.findById(id);
		
		if(user.isPresent())
		{
			Usuario u = user.get();
			
			u.setExcluido("S");
			
			u = usuarioRepository.saveAndFlush(u);
		}
		else
		{
			throw new UserNotExists();
		}
	}
}
