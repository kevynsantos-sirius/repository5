package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_Logomodelo")
@Getter
@Setter
public class Logomodelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLogoModelo")
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idModelo", nullable = false)
    private ModeloDocumento modeloDocumento;

    @Column(name = "codigo", nullable = false)
    private Integer codigo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idLogomodeloTipo", nullable = false)
    private LogomodeloTipo tipo;

    @Lob
    @Column(name = "arquivo")
    private byte[] arquivo;

    @Column(name = "tipoMIME")
    private String tipoMIME;
}