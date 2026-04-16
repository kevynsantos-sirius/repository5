package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_Recurso_Plano_Comunicacao")
@Getter
@Setter
public class RecursoPlanoComunicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPlanoComunicacaoVersao")
    private Long id;

    // =========================
    // RELACIONAMENTO COM PLANO
    // =========================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idPlanoComunicacao", nullable = false)
    private PlanoComunicacao planoComunicacao;

    // =========================
    // RELACIONAMENTO COM VERSÃO
    // =========================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idChecklistVersao", nullable = false)
    private ChecklistVersao checklistVersao;
    
    
    @ManyToOne
    @JoinColumn(name = "idRecurso")
    private Recurso recurso;
}