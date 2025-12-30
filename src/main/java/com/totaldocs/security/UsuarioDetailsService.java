package com.totaldocs.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Usuario usuario = repository.findByLogin(username)
            .orElseThrow(() ->
                new UsernameNotFoundException("Usuário não encontrado")
            );

        // Bloqueio / exclusão
        if (usuario.isFlgBloqueado() || "S".equalsIgnoreCase(usuario.getExcluido())) {
            throw new DisabledException("Usuário bloqueado");
        }

        return User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(usuario.getEhAdmin().equalsIgnoreCase("S") ? "ADMIN" : "USER")
                .build();
    }
}
