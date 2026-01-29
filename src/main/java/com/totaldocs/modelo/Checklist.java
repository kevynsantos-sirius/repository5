package com.totaldocs.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    
    @Column(name = "NomeDocumento", nullable = false, length = 50)
    private String nomeDocumento;

    @Column(name = "CentroCusto", nullable = false, length = 5)
    private String centroCusto;    
}
