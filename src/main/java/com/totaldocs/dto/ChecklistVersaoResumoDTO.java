package com.totaldocs.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChecklistVersaoResumoDTO {
	private String idChecklistVersao;
	private String idDemanda;
    private Integer versao;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private String nomeUsuario;
    private Integer status;
    private boolean atual;
}