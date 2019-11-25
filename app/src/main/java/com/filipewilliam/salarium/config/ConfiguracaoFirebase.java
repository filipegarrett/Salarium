package com.filipewilliam.salarium.config;

import com.filipewilliam.salarium.helpers.Base64Custom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;
    private static DatabaseReference firebase;

    public static DatabaseReference getFirebaseDatabase() {
        if (firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();

        }
        return firebase;

    }

    public static FirebaseAuth getFirebaseAutenticacao() {

        if (autenticacao == null) {
            autenticacao = FirebaseAuth.getInstance();

        }
        return autenticacao;

    }

    public static String idUsuario() {

        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        return idUsuario;

    }

}
