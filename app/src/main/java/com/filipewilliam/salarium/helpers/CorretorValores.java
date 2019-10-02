package com.filipewilliam.salarium.helpers;

public class CorretorValores {

    /*Essa classe existe apenas para converter valores a serem exibidos diretamente nos TextView. Dadas particularidades de como o Android funciona,
    a conversão usando o getCurrencyInstance retorna valores na formatação R$500, por exemplo*/

    public CorretorValores() {
    }

    public String tratarValores(String valor){
        String valorTratado = "";
        String valorOriginal = String.valueOf(valor);
        int indice = 1;

        for (int i = 0; i < valorOriginal.length(); i++) {
            valorTratado += valorOriginal.charAt(i);

            if (i == indice) {
                valorTratado += " ";
            }
        }
        return valorTratado;
    }

}
