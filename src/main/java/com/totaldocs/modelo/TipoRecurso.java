package com.totaldocs.modelo;

import com.totaldocs.enums.RecursoTipoCodigo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CL_TipoRecurso")
@Getter
@Setter
public class TipoRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTipoRecurso")
    private Integer id;

    @Column(name = "codigo")
    private Integer codigo;    // continua no banco como inteiro

    @Column(name = "descricao", length = 20)
    private String descricao;

    @Transient
    public RecursoTipoCodigo getEnum() {
        return RecursoTipoCodigo.fromCodigo(this.codigo);
    }
}