package com.totaldocs.dto;

import lombok.Data;

@Data
public class ItemArquivoDTO {

    private String id;
    private String name;
    private String observacao;
    private boolean temArquivo;
}