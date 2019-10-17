package com.filipewilliam.salarium.model;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Meta {

    private Double valor;
    private String data;

    public Meta() {
    }

    public Meta(Double valor, String data) {
        this.valor = valor;
        this.data = data;
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

    public void salvarMetaFirebase(String data){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(data).push().setValue(this);

    }


    public static void excluirMetaFirebase(String data){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        /* Map<String, Object> atualizacoes = new HashMap<>();
        atualizacoes.put("data", data);
        atualizacoes.put("valor", valor); */
        referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(data).removeValue();
        //referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(data).updateChildren(atualizacoes);
    }
}
