package com.filipewilliam.salarium.helpers;

import android.util.Base64;

public class Base64Custom {

    public static String codificarBase64(String dados){
        return Base64.encodeToString(dados.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");

    }

    public static String decodificarBase64(String dadosDecodificados){
        return new String(Base64.decode(dadosDecodificados, Base64.DEFAULT));

    }

}
