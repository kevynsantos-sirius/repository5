package com.totaldocs.dto;

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
    private boolean arquivoImpressao;

    private List<ItemArquivoDTO> logos;
    private List<ItemArquivoDTO> arquivosAdicionais;
    private List<ItemArquivoDTO> assinaturas;
}