package com.filipewilliam.salarium.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.LoginActivity;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ExcluirUsuarioDialog extends AppCompatDialogFragment {

    private FirebaseAuth autenticacao;
    private EditText editTextEmail;
    private EditText editTextSenha;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final Usuario usuario = new Usuario();
        final View view = inflater.inflate(R.layout.layout_excluir_usuario_dialog, null);
        editTextEmail = view.findViewById(R.id.editTextEmailExcluirUsuario);
        editTextSenha = view.findViewById(R.id.editTextSenhaExcluirUsuario);
        String mensagem = "Excluir a sua conta resultará na eliminação completa dos seus dados do Salarium." +
                "\n\nPara exlcuir sua conta você precisa confirmar seu e-mail e senha:";

        dialog.setView(view)
                .setTitle("Deseja mesmo excluir a sua conta?")
                .setMessage(mensagem)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Context context = getContext();
                        AuthCredential authCredential = EmailAuthProvider.getCredential(editTextEmail.getText().toString(), editTextSenha.getText().toString());
                        final FirebaseUser usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
                        if (usuarioFirebase != null){
                            usuarioFirebase.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    usuarioFirebase.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if(task1.isSuccessful()){
                                                usuario.removerUsuarioFirebase(Base64Custom.codificarBase64(editTextEmail.getText().toString()));
                                                Log.d("Tag", "Usuário excluído do Auth");

                                            }

                                        }
                                    });
                                }
                            });
                        }
                    }
                });
        return dialog.create();
    }

}
