package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class IntroducaoActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_introducao);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.corIntroFundo1)
                .fragment(R.layout.intro_1)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(R.color.corIntroFundo2)
                .fragment(R.layout.intro_2)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(R.color.corIntroFundo3)
                .fragment(R.layout.intro_3)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(R.color.corIntroFundo4)
                .fragment(R.layout.intro_4)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                .canGoForward(false)
                .build()
        );

    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado(); //tira a tela de cadastro da pilha e joga o usuário para a MainActivity
    }

    public void cadastrarUsuario(View view){
        startActivity(new Intent(this, CadastrarUsuarioActivity.class));

    }

    public void realizarLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));

    }

    public void verificarUsuarioLogado(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        final FirebaseUser usuario = autenticacao.getCurrentUser();

        if(usuario.isEmailVerified() && usuario != null){
            abrirMainActivity();

        }

    }

    public void abrirMainActivity(){
        startActivity(new Intent(this, MainActivity.class));

    }

}
