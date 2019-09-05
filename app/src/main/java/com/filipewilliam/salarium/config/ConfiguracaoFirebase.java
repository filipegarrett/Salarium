package com.filipewilliam.salarium.config;

import com.google.firebase.auth.FirebaseAuth;

public class ConfiguracaoFirebase {

    private static FirebaseAuth autenticacao;

    public static FirebaseAuth getFirebaseAutenticacao(){

        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();

        }
        return autenticacao;

    }

}
