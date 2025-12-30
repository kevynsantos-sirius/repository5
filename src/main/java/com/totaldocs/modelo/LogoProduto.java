package com.totaldocs.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "LogoProduto")
public class LogoProduto {
    @Id
    private Integer id;
    
    @Column(name = "Nome")
    private String nome;
}
