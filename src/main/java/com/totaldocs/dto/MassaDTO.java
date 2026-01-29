package com.totaldocs.dto;

public class MassaDTO {
    private int id;
    private String nomeMassaDados;
    private boolean temArquivo;
    private String observacao;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNomeMassaDados() {
		return nomeMassaDados;
	}
	public void setNomeMassaDados(String nomeMassaDados) {
		this.nomeMassaDados = nomeMassaDados;
	}
	public boolean isTemArquivo() {
		return temArquivo;
	}
	public void setTemArquivo(boolean temArquivo) {
		this.temArquivo = temArquivo;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}    
}
