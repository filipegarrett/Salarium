package com.filipewilliam.salarium.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
public class ConfiguracoesUsuarioFragment extends Fragment {

    private FirebaseAuth autenticacao;
    private Button buttonResetarSenha, buttonExcluirUsuario;

    public ConfiguracoesUsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracoes_usuario, container, false);

        buttonResetarSenha = view.findViewById(R.id.buttonReenviarSenha);
        buttonExcluirUsuario = view.findViewById(R.id.buttonExcluirConta);

        buttonResetarSenha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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

        });

        buttonExcluirUsuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                excluirUsuarioDialog();

            }
        });
        return view;
    }

    public void excluirUsuarioDialog() {
        ExcluirUsuarioDialog excluirUsuarioDialog = new ExcluirUsuarioDialog();
        excluirUsuarioDialog.show(getActivity().getSupportFragmentManager(), "dialog");
        buttonResetarSenha.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.corBotaoDesabilitado));
        buttonResetarSenha.setEnabled(false);

    }

}
