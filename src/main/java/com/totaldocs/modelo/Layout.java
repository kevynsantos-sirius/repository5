package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CL_Layout")
public class Layout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdCheckList", nullable = false)
    private Checklist checklist;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "NomeLayout", nullable = false, length = 100)
    private String nomeLayout;

    @Column(name = "TipoMIME", nullable = false, length = 40)
    private String tipoMIME;

    @Lob
    @Column(name = "ConteudoLayout", nullable = false)
    private byte[] conteudoLayout;

    @Lob
    @Column(name = "Observacao")
    private String observacao;
    
    @Column(name = "ViaServico", nullable = false)
    private boolean viaServico;
    
    @Column(name = "ViaTxt", nullable = false)
    private boolean viaTxt;

    @OneToMany(mappedBy = "layout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MassaDados> massasDados;
}
