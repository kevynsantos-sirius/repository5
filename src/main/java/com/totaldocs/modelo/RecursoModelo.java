package com.totaldocs.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CL_Recurso_Modelo")
public class RecursoModelo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idRecursoModelo")
	private Long id;
	
	@JoinColumn(name = "idRecurso")
	@ManyToOne
	private Recurso recurso;
	
	@JoinColumn(name = "idChecklistVersao")
	@ManyToOne
	private ChecklistVersao checklistVersao;
	
	@JoinColumn(name = "idModeloDocumento")
	@ManyToOne
	private ModeloDocumento modeloDocumento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Recurso getRecurso() {
		return recurso;
	}

	public void setRecurso(Recurso recurso) {
		this.recurso = recurso;
	}

	public ChecklistVersao getChecklistVersao() {
		return checklistVersao;
	}

	public void setChecklistVersao(ChecklistVersao checklistVersao) {
		this.checklistVersao = checklistVersao;
	}

	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}
	
	

}
