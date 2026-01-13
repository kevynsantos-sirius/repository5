package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CL_Checklist")
@Getter
@Setter
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdRamo", nullable = false)
    private Ramo ramo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "DataCadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "DataAtualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @Column(name = "IsIcatu", nullable = false)
    private boolean icatu;

    @Column(name = "IsRioGrande", nullable = false)
    private boolean rioGrande;

    @Column(name = "IsCaixa", nullable = false)
    private boolean caixa;

    @Column(name = "NomeDocumento", nullable = false, length = 50)
    private String nomeDocumento;

    @Column(name = "CentroCusto", nullable = false, length = 5)
    private String centroCusto;

    @Column(name = "Status", nullable = false)
    private Integer status;
    
    @Column(name = "IdDemanda", nullable = false)
    private String idDemanda;

    // --- RELACIONAMENTOS ONE TO MANY ---
    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Layout> layouts;

//    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TipoEnvio> tiposEnvio;
//
//    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ModeloDocumento> modelosDocumento;
    // getters e setters
    
}
