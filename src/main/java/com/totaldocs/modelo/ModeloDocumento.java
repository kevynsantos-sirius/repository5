package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    @JoinColumn(name = "IdChecklistVersao", nullable = false)
    private ChecklistVersao checklistVersao;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

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
    
    @Column(name = "EmailComDocumentoAnexoECarimbo", nullable = false)
    private boolean emailComDocumentoAnexoECarimbo;
    
    @Column(name = "EmailComDocumentoAnexoEcorpoEmail", nullable = false)
    private boolean emailComDocumentoAnexoEcorpoEmail;
    
    
    @Column(name = "EmailComDocumentoAnexoEarmazenamento", nullable = false)
    private boolean emailComDocumentoAnexoEarmazenamento;
    
    @Column(name = "EmailComDocumentoAnexoEarmazenamentoEemail", nullable = false)
    private boolean emailComDocumentoAnexoEarmazenamentoEemail;
    
    @Column(name = "DisponibilizacaoSMS", nullable = false)
    private boolean disponibilizacaoSMS;
    
    @Column(name = "DisponibilizacaoMeusDocumentosPDF", nullable = false)
    private boolean disponibilizacaoMeusDocumentosPDF;
    
    @Column(name = "DisponibilizacaoCorreioSimples", nullable = false)
    private boolean disponibilizacaoCorreioSimples;
    
    @Column(name = "DisponibilizacaoCorreioSimplesAR", nullable = false)
    private boolean disponibilizacaoCorreioSimplesAR;
    
    @Column(name = "Duplex")
    private Boolean duplex;
    
    @Column(name = "EmailComDocumentoAnexo")
    private Boolean emailComDocumentoAnexo;
    
    @Column(name = "AcabamentoAutoEnvelope", nullable = false)
    private boolean acabamentoAutoEnvelope;
    
    @Column(name = "AcabamentoManuseio", nullable = false)
    private boolean acabamentoManuseio;
    
    @Column(name = "AcabamentoInsercao", nullable = false)
    private boolean acabamentoInsercao;
    
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
    
    @Lob
    @Column(name = "RegrasAcesso")
    private String regrasAcesso;

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
}
