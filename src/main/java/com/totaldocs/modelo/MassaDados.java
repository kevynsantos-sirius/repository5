package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CL_MassaDados")
public class MassaDados {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdLayout", nullable = false)
    private Layout layout;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "NomeMassaDados", nullable = false, length = 100)
    private String nomeMassaDados;

    @Column(name = "TipoMIME", nullable = false, length = 40)
    private String tipoMIME;
        
    @Lob
    @Column(name = "ConteudoMassaDados", nullable = false)
    private byte[] conteudoMassaDados;

    @Lob
    @Column(name = "Observacao")
    private String observacao;
}