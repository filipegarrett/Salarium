package com.filipewilliam.salarium.model;

public class Gasto {

    private String descricao;
    private Double valor;
    private String descricaoCategoria;
    private String data;

    public Gasto() {
    }

    public Gasto(String descricao, Double valor, String descricaoCategoria, String data) {
        this.descricao = descricao;
        this.valor = valor;
        this.descricaoCategoria = descricaoCategoria;
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
