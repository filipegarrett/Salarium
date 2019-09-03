package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.filipewilliam.salarium.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class IntroducaoActivity extends IntroActivity {

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
                .build()
        );

    }

    public void cadastrarUsuario(View view){
        startActivity(new Intent(this, CadastrarUsuarioActivity.class));

    }

    public void realizarLogin(View view){
        startActivity(new Intent(this, LoginActivity.class));

    }

}
