package com.totaldocs.dto;


import java.util.List;
import lombok.Data;

@Data
public class LayoutDTO {
	private String id;
    private String nomeLayout;
    private String observacao;
    private boolean temArquivo;
    private List<MassaDTO> massasDados;
}
