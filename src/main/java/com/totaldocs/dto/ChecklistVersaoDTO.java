package com.totaldocs.dto;

import java.util.List;
import lombok.Data;

@Data
public class ChecklistVersaoDTO {

    // -------- Identificação --------
	private int idChecklistVersao;
	private int idChecklist;
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
    private ChecklistDTO checklistDTO;
    private UsuarioDTO usuario; 
    private List<LayoutDTO> layouts;
}