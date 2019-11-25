package com.filipewilliam.salarium.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetarSenhaDialog extends AppCompatDialogFragment {

    private EditText editTextEmail;
    private FirebaseAuth autenticacao;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_reset_senha_dialog, null);
        editTextEmail = view.findViewById(R.id.editTextResetEmail);

        dialog.setView(view)
                .setTitle("Informe o seu e-mail:")
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                        autenticacao.sendPasswordResetEmail(editTextEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(dialog.getContext(), "E-mail de redefinição de senha enviado!", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(dialog.getContext(), "Ocorreu um erro no envio do e-mail", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });
        return dialog.create();
    }
}
