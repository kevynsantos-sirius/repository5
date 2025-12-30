package com.totaldocs.dto;


import java.util.List;
import lombok.Data;

@Data
public class LayoutDTO {

    private String nomeLayout;
    private String observacao;
    private List<MassaDTO> massasDados;
}
