package com.filipewilliam.salarium.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.ConfiguracoesActivity;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;

public class ExcluirUsuarioDialog extends AppCompatDialogFragment {

    private EditText editTextEmail;
    private FirebaseAuth autenticacao;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.layout_excluir_usuario_dialog, null);

        dialog.setView(view)
                .setTitle("Deseja mesmo excluir a sua conta?")
                .setMessage("Excluir a sua conta resultará na eliminação completa dos seus dados do Salarium.")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                        FirebaseUser usuario = autenticacao.getCurrentUser();

                        usuario.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(dialog.getContext(), "E-mail de redefinição de senha enviado!", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), IntroActivity.class);
                                    getActivity().startActivity(intent);

                                } else {
                                    Toast.makeText(dialog.getContext(), "Ocorreu um erro na exclusão da sua conta", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
        return dialog.create();
    }

}
