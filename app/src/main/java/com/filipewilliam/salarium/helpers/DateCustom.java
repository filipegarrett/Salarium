package com.filipewilliam.salarium.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateCustom {

    public static String formatarData(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAno =  mes + ano;
        return mesAno;

    }

    public static String formatarMesAno(String data) {
        String mesAno = "";

        if(data.length() == 6){
            StringBuilder sb = new StringBuilder(data);
            String dataBarra = String.valueOf(sb.insert(2, "/"));

            SimpleDateFormat mesPadrao = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            SimpleDateFormat dataEntrada = new SimpleDateFormat("MM/yyyy");

            Date date = null;
            try {
                date = dataEntrada.parse(dataBarra);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mesAno = mesPadrao.format(date);
            mesAno = mesAno.substring(0, 1).toUpperCase() + mesAno.substring(1);

        }else{
            StringBuilder sb = new StringBuilder(data);
            String dataBarra = String.valueOf(sb.insert(1, "/"));

            SimpleDateFormat mesPadrao = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            SimpleDateFormat dataEntrada = new SimpleDateFormat("MM/yyyy");

            Date date = null;
            try {
                date = dataEntrada.parse(dataBarra);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mesAno = mesPadrao.format(date);
            mesAno = mesAno.substring(0, 1).toUpperCase() + mesAno.substring(1);

        }

        return mesAno;

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

    public static String formataMesAnoFirebase(String mesAnoExtenso){

        String mesAnoFirebase = "";

        SimpleDateFormat dataEntrada = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        SimpleDateFormat mesAnoF = new SimpleDateFormat("MMyyyy");

        Date date = null;
        try {
            date = dataEntrada.parse(mesAnoExtenso);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return mesAnoFirebase = mesAnoF.format(date);

    }

    public static int calculaDiferencaDias(Date d1, Date d2) {

        int daysdiff = 0;
        long diferenca = d2.getTime() - d1.getTime();
        long diffDays = diferenca / (24 * 60 * 60 * 1000);
        daysdiff = (int) diffDays;
        return daysdiff;
    }

}