package com.totaldocs.dto;

import java.io.File;
import java.util.List;
import lombok.Data;

@Data
public class ModeloDTO {

    private String id;
    private String observacao;
    private String regrasAcesso;
    
    private String nomeRecurso;

    private CamposBuscaDTO camposBusca;

    private List<String> tipoImpressao;
    private List<String> tipoAcabamento;

    private boolean temArquivo;

    private List<ItemArquivoDTO> logos;
    private List<ItemArquivoDTO> arquivosAdicionais;
    private List<ItemArquivoDTO> assinaturas;
    
    private List<String> disponibilizacao; 
    private List<String> emailOpcoes;
}
