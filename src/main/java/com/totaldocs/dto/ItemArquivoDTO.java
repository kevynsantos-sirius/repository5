package com.totaldocs.dto;

import lombok.Data;

@Data
public class ItemArquivoDTO {

    private String id;
    private String name;
    private String observacao;
    private boolean temArquivo;
    private Integer codigo;         // ordem / identificação interna

    // --- Tipo (tabela CL_LogomodeloTipo) ---
    private Integer tipo;           // ex.: 1 = LOGO, 2 = ARQUIVO_ADICIONAL, 3 = ASSINATURA
    private String descricaoTipo;   // ex.: "Logo", "Arquivo Adicional", "Assinatura"

    // --- Arquivo ---
    private byte[] arquivo;         // bytes
    private String mimeType;        // tipo MIME: image/png, application/pdf...
    private String nomeArquivo;     // útil para download/exibição
    
    private Boolean excluido;
}