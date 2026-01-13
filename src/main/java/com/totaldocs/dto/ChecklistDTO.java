package com.totaldocs.dto;

import java.util.List;
import lombok.Data;

@Data
public class ChecklistDTO {

    // -------- Identificação --------
	private int id;
    private String nomeDocumento;
    private Integer idRamo;
    private String nomeRamo;
    private String centroCusto;
    private Integer status;
    private Integer idUsuario;
    private boolean icatu;
    private boolean caixa;
    private boolean rioGrande;
    private String idDemanda;
    
    // -------- TI Layout --------
    private boolean temLayout;
    private boolean viaServico;
    private boolean viaTxt;

    private UsuarioDTO usuario; 
    private List<LayoutDTO> layouts;
}