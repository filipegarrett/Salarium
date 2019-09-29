package com.filipewilliam.salarium.helpers;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCustom {

    public static String formatarData(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAno =  mes + ano;
        return mesAno;

    }

    public static String formatarDiaMesAno(String data) {
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String diaMesAno = dia + mes + ano;
        return diaMesAno;
    }

    public static String retornaMesAno(){
        String mesAno = "";

        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH)+1;
        String mesString = Integer.toString(mes);
        String anoString = Integer.toString(ano);

        return mesAno = mesString + anoString;

    }

}
