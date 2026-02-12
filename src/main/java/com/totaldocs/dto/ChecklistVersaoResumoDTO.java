package com.totaldocs.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChecklistVersaoResumoDTO {
	private String idChecklistVersao;
	private String idDemanda;
    private Integer versao;
    private LocalDateTime dataCadastro;
    private String nomeUsuario;
    private Integer status;
    private boolean atual;
}