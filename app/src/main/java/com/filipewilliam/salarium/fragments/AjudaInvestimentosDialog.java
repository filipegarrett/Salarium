package com.filipewilliam.salarium.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.filipewilliam.salarium.R;

public class AjudaInvestimentosDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_ajuda_investimentos_dialog, null);
        String textoAjuda = "1- Para a Poupança, usamos a taxa de rendimento definida pelo Banco Central em https://www4.bcb.gov.br/pec/poupanca/poupanca.asp?frame=1" +
                            "\n\n2- A Poupança é um bom investimento para você começar a criar um hábito de economizar e se acostumar a como o mercado financeiro funciona" +
                            "\n\n3- Embora ofereça uma taxa de rendimento relativamente baixa, a Poupança é interessante porque permite resgate do capital a qualquer momento" +
                            "\n\n4- Outra vantagem desse tipo de investimento é a ausência de custos como imposto de renda";

        dialog.setView(view)
                .setTitle("Sobre as simulações")
                .setMessage(textoAjuda)
                .setPositiveButton("Entendi!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return dialog.create();
    }
}
