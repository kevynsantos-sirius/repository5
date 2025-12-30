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
@Table(name = "AssinatParc")
public class AssinatParc {
    @Id
    @Column(name = "CodAssParc")
    private String codAssParc;
}
