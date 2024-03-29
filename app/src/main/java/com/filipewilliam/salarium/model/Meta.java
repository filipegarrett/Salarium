package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Meta {

    public double valorMeta;
    public String mesMeta;

    public Meta() {
    }

    public Meta(double valorMeta, String mesMeta) {
        this.valorMeta = valorMeta;
        this.mesMeta = mesMeta;
    }

    public double getValorMeta() {
        return valorMeta;
    }

    public void setValorMeta(double valorMeta) {
        this.valorMeta = valorMeta;
    }

    public String getMesMeta() {
        return mesMeta;
    }

    public void setMesMeta(String mesMeta) {
        this.mesMeta = mesMeta;
    }

    public void salvarMetasFirebase(String mesAno) {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("metas").child(mesAno).push().setValue(this);

    }

    public void atualizarMetasFirebase(DatabaseReference referencia, double valor){
        referencia.setValue(valor);

    }

    public static void excluirMetasFirebase(String mesAno){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("metas").child(mesAno).removeValue();
    }
}
