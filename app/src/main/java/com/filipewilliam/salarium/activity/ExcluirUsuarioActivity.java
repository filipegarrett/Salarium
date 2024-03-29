package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

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

    private String email, senha, uid;
    private TextView textViewExcluindo;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_excluir_usuario);

        textViewExcluindo = findViewById(R.id.textViewExcluindoUsuario);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        senha = intent.getStringExtra("Senha");
        textViewExcluindo.setText("Excluindo usuário " + email + ", aguarde...");

        AuthCredential authCredential = EmailAuthProvider.getCredential(email, senha);
        final FirebaseUser usuarioFirebase = FirebaseAuth.getInstance().getCurrentUser();
        final Usuario usuario = new Usuario();
        if (usuarioFirebase != null) {
            usuarioFirebase.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    uid = autenticacao.getUid();
                    referenciaFirebase.child("excluidos").child(uid).push().setValue(true);
                    textViewExcluindo.setText("Excluindo usuário " + email + ", aguarde... \n\nEncerrando...");

                    usuarioFirebase.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (task1.isSuccessful()) {
                                usuario.removerUsuarioFirebase(Base64Custom.codificarBase64(email));
                                Log.d("Tag", "Usuário excluído do Auth");
                                try {
                                    Thread.sleep(3500);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                finishAffinity();
                                android.os.Process.killProcess(android.os.Process.myPid());
                                //System.exit(0);

                            }

                        }
                    });
                }
            });
        }

    }
}