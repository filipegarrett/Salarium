package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Transacao {

    private String tipo;
    private String descricao;
    private String categoria;
    private Double valor;
    private String data;

    //Construtores
    public Transacao(){

    }

    //métodos get e set

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    //salva no firebase
    public void salvarTransacao(String data){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        String mesAno = DateCustom.formatarData(data);
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("transacao").child(mesAno).push().setValue(this);
    }
}
