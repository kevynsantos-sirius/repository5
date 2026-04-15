package com.totaldocs.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArquivoPlanoDTO {

    private String id;

    private List<ItemArquivoDTO> file; // ← arquivo real

    private String nomeArquivo;

    private String observacao;

    private Boolean excluido;
}