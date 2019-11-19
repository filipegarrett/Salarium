package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ExcluirUsuarioActivity extends AppCompatActivity {

    private ProgressBar progressBarExcluirUsuario;
    private String email, senha, uid;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_excluir_usuario);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        senha = intent.getStringExtra("Senha");

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, senha);
        final FirebaseUser usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        final Usuario usuario = new Usuario();
        if (usuarioFirebase != null) {
            usuarioFirebase.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uid = autenticacao.getUid();
                    referenciaFirebase.child("excluidos").child(uid).push().setValue(true);
                    usuarioFirebase.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                usuario.removerUsuarioFirebase(Base64Custom.codificarBase64(email));
                                Log.d("Tag", "Usuário excluído do Auth");
                                finishAffinity();
                                //System.exit(0);

                            }

                        }
                    });
                }
            });
        }

    }
}