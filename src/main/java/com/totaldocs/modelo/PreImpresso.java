package com.totaldocs.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PreImpresso")
public class PreImpresso {
    @Id
    private Integer id;
}
