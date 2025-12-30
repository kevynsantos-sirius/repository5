package com.totaldocs.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "AssinatIcatu")
public class AssinatIcatu {
    @Id
    @Column(name = "CodAssIH")
    private String codigoAssIH;
}
