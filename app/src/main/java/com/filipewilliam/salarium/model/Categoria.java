package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Categoria {

    private String descricaoCategoria;

    public Categoria() {
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public void salvarCategoriaRecebimento(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("categorias_recebimentos").push().setValue(this);
    }

    public void salvarCategoriaGasto(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("categorias_gastos").push().setValue(this);
    }
}
