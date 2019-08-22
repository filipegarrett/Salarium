package com.filipewilliam.salarium.model;

import java.util.Date;

public class Recebi {

    private String descricao;
    private Double valor;
    private Categoria descricaoCategoria;
    private String data;
    private boolean repete;

    public Recebi() {
    }

    public Recebi(String descricao, Double valor, Categoria descricaoCategoria, String data, boolean repete) {
        this.descricao = descricao;
        this.valor = valor;
        this.descricaoCategoria = descricaoCategoria;
        this.data = data;
        this.repete = repete;
    }

    public Recebi(String descricao, Double valor, String data, boolean repete) {
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.repete = repete;
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

    public Categoria getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(Categoria descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isRepete() {
        return repete;
    }

    public void setRepete(boolean repete) {
        this.repete = repete;
    }
}
