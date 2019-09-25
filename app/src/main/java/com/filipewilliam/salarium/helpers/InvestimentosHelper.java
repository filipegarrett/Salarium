package com.filipewilliam.salarium.helpers;

import android.icu.text.NumberFormat;

public class InvestimentosHelper {

    public InvestimentosHelper() {
    }

    public double simularPoupanca(double valorSimulacao, double valorAporteMensal, double taxaPoupanca, int mesesPoupanca){
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

    public String tratarValores(Double valor){
        String valorOriginal = String.valueOf(NumberFormat.getCurrencyInstance().format(valor));

        String valorTratado = "";
        int indice = 1;

        for (int i = 0; i < valorOriginal.length(); i++) {
            valorTratado += valorOriginal.charAt(i);

            if (i == indice) {
                valorTratado += " ";
            }
        }

        return valorTratado;
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


}
