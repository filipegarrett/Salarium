package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.fragments.ResetarSenhaDialog;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Usuario;
import com.filipewilliam.salarium.service.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextSenha;
    private TextView resetarSenha, reenviarEmail;
    private Button botaoEntrar, reset;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        botaoEntrar = findViewById(R.id.buttonEntrar);
        resetarSenha = findViewById(R.id.textViewResetarSenha);
        reenviarEmail = findViewById(R.id.textViewReenviarEmail);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String email = editTextEmail.getText().toString();
                String senha = editTextSenha.getText().toString();

                if(!email.isEmpty()){
                    if(!senha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(email);
                        usuario.setSenha(senha);
                        validarLogin();

                    }else {
                        Toast.makeText(LoginActivity.this, "Preencha o campo senha", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "Preencha o campo e-mail", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetarSenha.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                resetarSenha();

            }
        });

        reenviarEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reenviarEmail();
            }
        });

    }

    public void validarLogin(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    if(autenticacao.getCurrentUser().isEmailVerified()){
                        recuperarToken();
                        abrirMainActivity();

                    }else{
                        Toast.makeText(LoginActivity.this, "Por favor, confirme sua conta por meio do e-mail", Toast.LENGTH_LONG).show();
                    }
                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-mail ou senha informados inválidos";
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário inválido";
                    }catch (Exception e){
                        excecao = "Erro ao realizar login: " + e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void abrirMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void resetarSenha() {
        ResetarSenhaDialog resetarSenhaDialog = new ResetarSenhaDialog();
        resetarSenhaDialog.show(getSupportFragmentManager(), "dialog");

    }

    public void reenviarEmail(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser user = autenticacao.getCurrentUser();

        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,"Enviamos um novo e-mail para você!", Toast.LENGTH_LONG).show();

                    }else {
                        Toast.makeText(LoginActivity.this, "Tente novamente mais tarde", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }else{
            Toast.makeText(LoginActivity.this, "Você precisa antes criar uma conta de usuário", Toast.LENGTH_LONG).show();
        }

    }

    public void recuperarToken(){

        final String usuarioToken = MyFirebaseMessagingService.retornaToken(getApplicationContext());
        final String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
        usuario.salvarToken(usuarioToken, idUsuario);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}