package com.filipewilliam.salarium.model;

public class Transacao {

    private String tipoTransacao;
    private String categoriaTransacao;
    private String valorTransacao;
    private String dataTransacao;

    public Transacao(){

    }

    public Transacao(String tipoTransacao, String categoriaTransacao, String valorTransacao, String dataTransacao) {
        this.tipoTransacao = tipoTransacao;
        this.categoriaTransacao = categoriaTransacao;
        this.valorTransacao = valorTransacao;
        this.dataTransacao = dataTransacao;
    }

    public String getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(String tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }

    public String getCategoriaTransacao() {
        return categoriaTransacao;
    }

    public void setCategoriaTransacao(String categoriaTransacao) {
        this.categoriaTransacao = categoriaTransacao;
    }

    public String getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(String valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public String getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(String dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
}
