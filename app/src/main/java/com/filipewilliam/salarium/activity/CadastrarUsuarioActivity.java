package com.filipewilliam.salarium.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.model.Usuario;
import com.filipewilliam.salarium.watcher.MaskWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class CadastrarUsuarioActivity extends AppCompatActivity {

    private EditText editTextNome, editTextDataNascimento, editTextEmail, editTextSenha;
    private Button botaoCadastrarUsuario;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private MaskWatcher maskDataNascimento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        editTextNome = findViewById(R.id.editTextNome);
        editTextDataNascimento = findViewById(R.id.editTextDadaNascimento);
        editTextDataNascimento.addTextChangedListener(maskDataNascimento.aplicarMaskDataNascimento());
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        botaoCadastrarUsuario = findViewById(R.id.buttonEntrar);

        botaoCadastrarUsuario.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String nome = editTextNome.getText().toString();
                String dataNascimento = editTextDataNascimento.getText().toString();
                String email = editTextEmail.getText().toString();
                String senha = editTextSenha.getText().toString();

                if(!nome.isEmpty()){
                    if(!dataNascimento.isEmpty()){
                        if(!email.isEmpty()){
                            if(!senha.isEmpty()){

                                usuario = new Usuario();
                                usuario.setNome(nome);
                                usuario.setDataNascimento(dataNascimento);
                                usuario.setEmail(email);
                                usuario.setSenha(senha);
                                cadastrarUsuario();

                            }else{
                                Toast.makeText(CadastrarUsuarioActivity.this, "Preencha uma senha válida!", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(CadastrarUsuarioActivity.this, "Preencha o seu e-mail!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(CadastrarUsuarioActivity.this, "Preencha a sua data de nascimento!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(CadastrarUsuarioActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void cadastrarUsuario(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(),  usuario.getSenha()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = autenticacao.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CadastrarUsuarioActivity.this, "Um e-mail de confirmação foi enviado para" + user.getEmail(), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(CadastrarUsuarioActivity.this, "Ocorreu uma falha na verificação de seu e-mail", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                    finish();

                }else{
                    String excecao = "";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Senha inválida. Escolha uma senha mais forte";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Por favor, escolha um e-mail válido";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Já existe um usuário cadastrado com o e-mail informado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar o usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastrarUsuarioActivity.this, excecao, Toast.LENGTH_SHORT).show();
                }
            }
        });

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
