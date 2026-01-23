package com.totaldocs.modelo;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_ChecklistVersao")
@Getter
@Setter
public class ChecklistVersao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer idChecklistVersao;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "IdChecklist", nullable = false)
    private Checklist checklist;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuario", nullable = false)
    private Usuario usuario;
    
    @Column(name = "Status", nullable = false)
    private Integer status;
    
    @Column(name = "IdDemanda", nullable = false)
    private String idDemanda;

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
    
    @Column(name = "Versao" )
    private int versao;
    
    
    // --- RELACIONAMENTOS ONE TO MANY ---
    @OneToMany(mappedBy = "checklistVersao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Layout> layouts;
}
