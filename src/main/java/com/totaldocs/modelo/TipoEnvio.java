package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CL_TipoEnvio")
public class TipoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdCheckList", nullable = false)
    private Checklist checklist;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "Tipo", nullable = false)
    private Integer tipo;

    @Column(name = "NomeTemplate", nullable = false, length = 100)
    private String nomeTemplate;

    @Column(name = "TipoMIME", nullable = false, length = 40)
    private String tipoMIME;

    @Lob
    @Column(name = "ConteudoTemplate", nullable = false)
    private byte[] conteudoTemplate;

    @Lob
    @Column(name = "Observacao")
    private String observacao;
}
