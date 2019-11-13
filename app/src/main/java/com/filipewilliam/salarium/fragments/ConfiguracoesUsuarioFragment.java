package com.filipewilliam.salarium.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfiguracoesUsuarioFragment extends PreferenceFragmentCompat {

    private FirebaseAuth autenticacao;

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
        FirebaseUser usuario = autenticacao.getCurrentUser();
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
        ExcluirUsuarioDialog excluirUsuarioDialog = new ExcluirUsuarioDialog();
        excluirUsuarioDialog.show(getActivity().getSupportFragmentManager(), "dialog");

    }

}
