package com.totaldocs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogomodeloTipoCodigo {

    LOGO(1),
    ARQUIVO_ADICIONAL(2),
    ASSINATURA(3);

    private final int codigo;

    public static LogomodeloTipoCodigo fromCodigo(int codigo) {
        for (var t : values()) {
            if (t.codigo == codigo) return t;
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}
