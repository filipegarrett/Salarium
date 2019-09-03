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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CadastrarUsuarioActivity extends AppCompatActivity {

    private EditText editTextNome, editTextDataNascimento, editTextEmail, editTextSenha;
    private Button botaoCadastrarUsuario;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);

        editTextNome = findViewById(R.id.editTextNome);
        editTextDataNascimento = findViewById(R.id.editTextDadaNascimento);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSenha = findViewById(R.id.editTextSenha);
        botaoCadastrarUsuario = findViewById(R.id.buttonCadastrar);

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
                    Toast.makeText(CadastrarUsuarioActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(CadastrarUsuarioActivity.this, "Erro ao realizar cadastro de usuário!", Toast.LENGTH_SHORT).show();
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
