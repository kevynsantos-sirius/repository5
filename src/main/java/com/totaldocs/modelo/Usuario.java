package com.totaldocs.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Usuarios")
@Getter
@Setter
public class Usuario {
	@Id
	@Column(name = "Id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@Column(name = "IdUsuarioResponsavel")
	private Integer idUsuarioResponsavel;
	
	@Column(name = "Login")
	private String login;
	
	@Column(name = "Nome")
	private String nome;
	
	@Column(name = "Email")
	private String email;
	
	@Column(name = "Setor")
	private String setor;
	
	@Column(name = "Senha")
	private String senha;
	
	@Column(name = "Excluido")
	private String excluido;
	
	@Column(name = "Permissoes")
	private String permissoes;
	
	@Column(name = "EhAdmin")
	private String ehAdmin;
	
	@Column(name = "RecLog")
	private String recLog;
	
	@Column(name = "FlgBloqueado")
	private boolean flgBloqueado;
}