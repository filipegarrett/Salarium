package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void salvarGasto(String data){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        String mesAno = DateCustom.formatarData(data);
        FirebaseDatabase referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.getReference().child("gastos").child(idUsuario).child(mesAno).push().setValue(this);
    }
}
