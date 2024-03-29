package com.filipewilliam.salarium.helpers;

import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.icu.util.Currency;

import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Locale;

public class FormatarValoresHelper extends ValueFormatter {

    public FormatarValoresHelper() {
    }

    public double simularPoupanca(double valorSimulacao, double valorAporteMensal, int mesesPoupanca){
        double taxaPoupanca = 0.003434;
        int i;

        valorSimulacao += valorSimulacao * taxaPoupanca;

        for(i = 0; i < (mesesPoupanca - 1); i++){

            valorSimulacao = (valorSimulacao * taxaPoupanca) + valorSimulacao + valorAporteMensal;

        }

        return valorSimulacao;
    }

    public String rendimentoPoupanca(double valorSimulacao, double valorOriginal, double valorDepositoMensal, int quantidadeMeses){
        double rendimento = 0.0;
        String rendimentoResultado = "";

        rendimento = valorSimulacao - valorOriginal - (valorDepositoMensal * (quantidadeMeses - 1));
        rendimentoResultado = tratarValores(rendimento);

        return rendimentoResultado;
    }

    public static String tratarValores(double d){

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getInstance();
        Locale locale = Locale.getDefault();
        String symbol = Currency.getInstance(locale).getSymbol(locale);
        fmt.setGroupingUsed(true);
        fmt.setPositivePrefix(symbol + " ");
        fmt.setNegativePrefix("-" + symbol + " ");
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(d);
    }

    public Double simularCDBPrefixado(double valorSimulacao){
        double taxaCDB = 0.07;
        int i;

        for(i = 0; i < 5; i++){
            valorSimulacao = (valorSimulacao * taxaCDB) + valorSimulacao;
        }

        return valorSimulacao;
    }

    public String rendimentoCDBPrefixado(double valorSimulacao, double valorOriginal){
        double taxaIR = 0.15;

        double valorImposto = (valorSimulacao - valorOriginal) * taxaIR;
        double rendimento = valorSimulacao - valorOriginal - valorImposto;

        String resultadoCDB = tratarValores(rendimento);
        return resultadoCDB;
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {

        DecimalFormat fmt = (DecimalFormat) NumberFormat.getInstance();
        Locale locale = Locale.getDefault();
        String symbol = Currency.getInstance(locale).getSymbol(locale);
        fmt.setGroupingUsed(true);
        fmt.setPositivePrefix(symbol + " ");
        fmt.setNegativePrefix("-" + symbol + " ");
        fmt.setMinimumFractionDigits(2);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(value);

    }

}
