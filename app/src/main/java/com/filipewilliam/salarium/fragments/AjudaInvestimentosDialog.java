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
        final View view = inflater.inflate(R.layout.layout_excluir_usuario_dialog, null);

        dialog.setView(view)
                .setTitle("Sobre as simulações")
                .setMessage("1- Para a Poupança, usamos a taxa de rendimento definida pelo Banco Central em /n" +
                        "2- Calculos de CDB Préfixado consideram um rendimento de 7% ao ano e imposto de renda de 15% sobre o rendimento da aplicação /n" +
                        "3- Caso você opte por um investimento de renda fixa, é importante considerar que as taxas e prazos de rendimento variam de instituição para instituição /n" +
                        "4- Outro ponto a ser considerado é que instituições financeiras que oferecem esse investimento também irão cobrar uma taxa de administração mensalmente")
                .setPositiveButton("Entendi!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return dialog.create();
    }
}
