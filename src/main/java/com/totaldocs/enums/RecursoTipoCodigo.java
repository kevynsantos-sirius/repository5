package com.totaldocs.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecursoTipoCodigo {

    LOGO(1),
    ARQUIVO_ADICIONAL(2),
    ASSINATURA(3),
    IMPRESSAO(4),
    PLANO_COMUNICACAO(5);

    private final int codigo;

    public static RecursoTipoCodigo fromCodigo(int codigo) {
        for (var t : values()) {
            if (t.codigo == codigo) return t;
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}
