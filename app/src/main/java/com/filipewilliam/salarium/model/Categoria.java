package com.filipewilliam.salarium.model;

public class Categoria {

    private String descricaoCategoria;

    public Categoria() {
    }

    public Categoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }
}
