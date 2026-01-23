package com.totaldocs.dto;

import java.util.List;
import lombok.Data;

@Data
public class ChecklistDTO {

    // -------- Identificação --------
	private int id;
    private String nomeDocumento;
    private String nomeRamo;
    private String centroCusto;
//    private String idDemanda;
    
    // -------- TI Layout --------
//    private boolean temLayout;
//    private boolean viaServico;
//    private boolean viaTxt;

//    private UsuarioDTO usuario; 
//    private List<LayoutDTO> layouts;
}