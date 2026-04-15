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
	
	@JoinColumn(name = "idRecurso")
	@OneToMany
	private List<Recurso> recursos;
	
	@JoinColumn(name = "idChecklistVersao")
	@ManyToOne
	private ChecklistVersao checklistVersao;

}
