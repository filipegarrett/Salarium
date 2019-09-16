package com.filipewilliam.salarium.helpers;

public class DateCustom {

    public static String formatarData(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAno =  mes + ano;
        return mesAno;

    }
}
