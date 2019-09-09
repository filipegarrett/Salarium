package com.filipewilliam.salarium.model;

import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {

    private String idUsuario, senha;
    String noe, dataNascimento, email;

    public Usuario() {
    }

    public void salvarUsuarioFirebase(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child(this.idUsuario)
                .setValue(this);
    }

    public void removerUsuarioFirebase(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarios")
                .child(this.idUsuario).removeValue();
    }

    public void AlterarUsuario(){

    }

    @Exclude
    public String getIdUsuario() { return idUsuario; }

    public String getNome() {
        return noe;
    }

    public void setNome(String nome) {
        this.noe = nome;
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

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
