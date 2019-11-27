package com.filipewilliam.salarium.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.ConfiguracoesActivity;
import com.filipewilliam.salarium.activity.ContasVencerActivity;
import com.filipewilliam.salarium.activity.ExcluirUsuarioActivity;
import com.filipewilliam.salarium.activity.MainActivity;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracoesUsuarioFragment extends PreferenceFragmentCompat {

    private FirebaseAuth autenticacao;
    private EditText editTextExcluirEmail;
    private EditText editTextExcluirSenha;

    public ConfiguracoesUsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);



        findPreference("resetarSenha").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                resetarSenha();
                return true;
            }
        });

        findPreference("excluirUsuario").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                excluirUsuario();
                return true;

            }
        });

        /*findPreference("notificacoes").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });*/

    }

    public void resetarSenha(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final FirebaseUser usuario = autenticacao.getCurrentUser();
        autenticacao.sendPasswordResetEmail(usuario.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "E-mail de redefinição de senha enviado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Ocorreu um erro no envio do e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void excluirUsuario() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.layout_excluir_usuario_dialog, null);
        editTextExcluirEmail = viewDialog.findViewById(R.id.editTextEmailExcluirUsuario);
        editTextExcluirSenha = viewDialog.findViewById(R.id.editTextSenhaExcluirUsuario);
        String mensagem = "Excluir a sua conta resultará na eliminação completa dos seus dados do Salarium." +
                "\n\nPara excluir sua conta você precisa confirmar seu e-mail e senha:";

        alertDialog.setView(viewDialog)
                .setTitle("Deseja mesmo excluir a sua conta?")
                .setMessage(mensagem)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("SIM!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluirActivity(editTextExcluirEmail.getText().toString(), editTextExcluirSenha.getText().toString());

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void excluirActivity(String email, String senha){

        Intent intent = new Intent(getContext(), ExcluirUsuarioActivity.class);
        intent.putExtra("Email", email).putExtra("Senha", senha);
        startActivity(intent);

    }

}
