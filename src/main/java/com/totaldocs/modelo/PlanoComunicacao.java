package com.totaldocs.modelo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "CL_Plano_Comunicacao")
public class PlanoComunicacao {
	
	@Id
	@Column(name = "idPlano")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "observacao")
	private String observacao;
	
	@Column(name = "nome")
	private String nome;
	
	@JoinColumn(name = "idRecurso")
	@OneToMany
	private List<Recurso> recursos;
	
	@JoinColumn(name = "idChecklistVersao")
	@ManyToOne
	private ChecklistVersao checklistVersao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<Recurso> getRecursos() {
		return recursos;
	}

	public void setRecursos(List<Recurso> recursos) {
		this.recursos = recursos;
	}

	public ChecklistVersao getChecklistVersao() {
		return checklistVersao;
	}

	public void setChecklistVersao(ChecklistVersao checklistVersao) {
		this.checklistVersao = checklistVersao;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	

}
