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

    private List<ItemArquivoDTO> logos;
    private List<ItemArquivoDTO> arquivosAdicionais;
    private List<ItemArquivoDTO> assinaturas;

    private List<String> disponibilizacao;
    private List<String> emailOpcoes;

    private List<ItemArquivoDTO> arquivosImpressao;

    private Boolean duplex;
    private Boolean isImpresso;


    // ---------------------------------------------------------
    // 🔽 🔽 🔽 PROPRIEDADES BOOLEANS UTILIZADAS PELOS SETTERS
    // ---------------------------------------------------------

    // Tipo de Acabamento
    private boolean acabamentoAutoEnvelope;
    private boolean acabamentoManuseio;
    private boolean acabamentoInsercao;

    // Disponibilização
    private boolean disponibilizacaoCorreioSimples;
    private boolean disponibilizacaoCorreioSimplesAR;
    private boolean crc; // (m.getDisponibilizacao().contains("impressaoSobDemanda"))
    private boolean disponibilizacaoMeusDocumentosPDF;
    private boolean disponibilizacaoSMS;

    // Email
    private boolean emailComDocumentoAnexo;
    private boolean emailComDocumentoAnexoEarmazenamento;
    private boolean emailComDocumentoAnexoEcorpoEmail;
    private boolean emailComDocumentoAnexoEarmazenamentoEemail;
    private boolean emailComDocumentoAnexoECarimbo;
    
    private boolean isCRC;
}