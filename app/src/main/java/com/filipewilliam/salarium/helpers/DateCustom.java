package com.filipewilliam.salarium.helpers;

import java.text.ParseException;
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

    public static Date retornaDataHojeDateFormat(){

        Calendar calendario = Calendar.getInstance();
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH) + 1;
        int dia = calendario.get(Calendar.DAY_OF_MONTH);
        String mesString = Integer.toString(mes);
        String anoString = Integer.toString(ano);
        String diaString = Integer.toString(dia);
        String dataString = diaString + "/" + mesString + "/" + anoString;
        Date hoje = null;
        try {
            hoje = new SimpleDateFormat("dd/MM/yyyy").parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return hoje;

    }

    public static Long stringParaTimestamp(String data){
        try {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formatter.parse(data);
            long saidaData = date.getTime()/1000L;
            String dataString = Long.toString(saidaData);
            long timestampD = Long.parseLong(dataString) * 1000;
            return timestampD;

        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }

}