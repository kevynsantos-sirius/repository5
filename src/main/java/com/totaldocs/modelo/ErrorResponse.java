package com.totaldocs.modelo;

public class ErrorResponse {

    private String mensagem;
    private String detalhe;
    private int status;

    public ErrorResponse() {}

    public ErrorResponse(String mensagem, String detalhe, int status) {
        this.mensagem = mensagem;
        this.detalhe = detalhe;
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public int getStatus() {
        return status;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
