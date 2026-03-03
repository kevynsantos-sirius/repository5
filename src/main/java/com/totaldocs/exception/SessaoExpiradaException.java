package com.totaldocs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class SessaoExpiradaException extends RuntimeException {

    public SessaoExpiradaException() {
        super("Sessão expirada");
    }
}
