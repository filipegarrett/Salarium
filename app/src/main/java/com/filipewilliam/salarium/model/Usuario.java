package com.filipewilliam.salarium.model;

import android.support.annotation.NonNull;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Usuario {

    private String idUsuario, senha;
    private String nome, dataNascimento, email;
    private Double gastoTotal = 0.0, recebimentoTotal = 0.0;
    private String token;

    public Usuario() {
    }

    public void salvarUsuarioFirebase(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child(this.idUsuario)
                .setValue(this);
    }

    public void salvarToken(final String token, String id){
        final String user = id;
        final DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    if(dataSnapshot1.child("token").exists()){
                        System.out.println(dataSnapshot1.child("token"));
                        firebase.child("usuarios").child(user).child("token").setValue(token);

                    }else {
                        Map<String, Object> campoToken = new HashMap<String, Object>();
                        campoToken.put("token", token);
                        firebase.child("usuarios").child(user).updateChildren(campoToken);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        /*if(firebase.child("usuarios").child(this.idUsuario).child("token").toString().isEmpty()){
            Map<String, Object> campoToken = new HashMap<String, Object>();
            campoToken.put("token", token);
            firebase.child("usuarios").child(this.idUsuario).updateChildren(campoToken);

        }

        firebase.child("usuarios")
                .child(this.idUsuario)
                .child("token")
                .setValue(this.token);*/

    }

    public void removerUsuarioFirebase(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child(this.idUsuario).removeValue();
    }

    @Exclude
    public String getIdUsuario() { return idUsuario; }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getRecebimentoTotal() {
        return recebimentoTotal;
    }

    public void setRecebimentoTotal(Double recebimentoTotal) {
        this.recebimentoTotal = recebimentoTotal;
    }

    public Double getGastoTotal() {
        return gastoTotal;
    }

    public void setGastoTotal(Double gastoTotal) {
        this.gastoTotal = gastoTotal;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
