package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "CL_ModeloDocumento")
public class ModeloDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdCheckList", nullable = false)
    private Checklist checklist;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdLogoCapa")
    private LogoCapa logoCapa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdLogoProduto")
    private LogoProduto logoProduto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodAssIH")
    private AssinatIcatu assinaturaIcatu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CodAssParc")
    private AssinatParc assinaturaParc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPreImpresso")
    private PreImpresso preImpresso;

    @Column(name = "NomeRecurso", nullable = false, length = 100)
    private String nomeRecurso;

    @Column(name = "TipoMIME", nullable = false, length = 40)
    private String tipoMIME;

    @Lob
    @Column(name = "ConteudoRecurso", nullable = false)
    private byte[] conteudoRecurso;

    @Lob
    @Column(name = "Observacao")
    private String observacao;

    @Column(name = "IsImpresso", nullable = false)
    private boolean isImpresso;

    @Column(name = "IsCRC", nullable = false)
    private boolean isCRC;

    @Column(name = "Duplex")
    private Boolean duplex;

    @Column(name = "Acabamento")
    private Integer acabamento;

    @Column(name = "IsArmazenamento", nullable = false)
    private boolean isArmazenamento;

    @Column(name = "TempoArmazenamento", nullable = false)
    private Integer tempoArmazenamento;

    @Column(name = "AcessoBackOffice")
    private Boolean acessoBackOffice;

    @Lob
    @Column(name = "CamposBuscaBackOffice")
    private String camposBuscaBackOffice;

    @Column(name = "AcessoCliente")
    private Boolean acessoCliente;

    @Lob
    @Column(name = "CamposBuscaCliente")
    private String camposBuscaCliente;

    @Column(name = "AcessoCorretor")
    private Boolean acessoCorretor;

    @Lob
    @Column(name = "CamposBuscaCorretor")
    private String camposBuscaCorretor;

    @Column(name = "AcessoEstipulante")
    private Boolean acessoEstipulante;

    @Lob
    @Column(name = "CamposBuscaEstipulante")
    private String camposBuscaEstipulante;

    @Column(name = "AcessoSubEstipulante")
    private Boolean acessoSubEstipulante;

    @Lob
    @Column(name = "CamposBuscaSubEstipulante")
    private String camposBuscaSubEstipulante;

    // getters e setters
}
