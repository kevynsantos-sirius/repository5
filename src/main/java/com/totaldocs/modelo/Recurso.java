package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_Recurso")
@Getter
@Setter
public class Recurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRecurso")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idModelo")
    private ModeloDocumento modeloDocumento;

    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "codigo",                 // coluna na tabela atual
        referencedColumnName = "codigo", // coluna na tabela
        insertable = false,
        updatable = false
    )
    private TipoRecurso tipo;

    @Lob
    @Column(name = "arquivo")
    private byte[] arquivo;

    @Column(name = "tipoMIME")
    private String tipoMIME;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idCheckListVersao", nullable = false)
    private ChecklistVersao checklistVersao;
    
    @Column(name = "nomeRecurso")
    private String nomeRecurso;
}