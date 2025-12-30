package com.totaldocs.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Ramo")
@Getter
@Setter
public class Ramo {
    @Id
    @Column(name = "IdRamo")
    private Integer idRamo;
    @Column(name = "NomeRamo")
    private String nomeRamo;
}
