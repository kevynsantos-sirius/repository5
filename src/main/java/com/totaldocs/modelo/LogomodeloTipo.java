package com.totaldocs.modelo;

import com.totaldocs.enums.LogomodeloTipoCodigo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_logomodelotipo")
@Getter
@Setter
public class LogomodeloTipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idLogomodeloTipo")
    private Integer id;

    @Column(name = "codigo")
    private Integer codigo;    // continua no banco como inteiro

    @Column(name = "descricao", length = 20)
    private String descricao;

    @Transient
    public LogomodeloTipoCodigo getEnum() {
        return LogomodeloTipoCodigo.fromCodigo(this.codigo);
    }
}