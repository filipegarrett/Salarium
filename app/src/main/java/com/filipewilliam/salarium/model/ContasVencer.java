package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class ContasVencer {

    public String categoria;
    public Double valor;
    public String dataVencimento;
    public Long timestampVencimento;

    public ContasVencer() {
    }

    public ContasVencer(String categoria, Double valor, String dataVencimento, Long timestampVencimento) {
        this.categoria = categoria;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.timestampVencimento = timestampVencimento;
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

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Long getTimestampVencimento() {
        return timestampVencimento;
    }

    public void setTimestampVencimento(Long timestampVencimento) {
        this.timestampVencimento = timestampVencimento;
    }

    public void salvarContasAVencer(){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaFirebase.child("usuarios").child(idUsuario).child("contas-a-vencer").push().setValue(this);
    }

}
