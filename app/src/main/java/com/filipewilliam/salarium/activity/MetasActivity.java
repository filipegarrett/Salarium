package com.filipewilliam.salarium.activity;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.MetasAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.model.Meta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MetasActivity extends AppCompatActivity {

    private EditText editTextValorMeta;
    private DateCustom dateCustom;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private EditText editTextDataSelecionada;
    private Button buttonCriarMeta;
    private List<Meta> listaMetas = new ArrayList<Meta>();
    private RecyclerView recyclerViewMetas;
    private String mesAtual = dateCustom.retornaMesAno();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //ativa a seta do menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //recupera as metas criadas
        recuperarMetas();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);

        recyclerViewMetas = findViewById(R.id.recyclerViewMetasCriadas);
        editTextDataSelecionada = findViewById(R.id.editTextDataMeta);
        editTextValorMeta = findViewById(R.id.editTextValorMeta);
        buttonCriarMeta = findViewById(R.id.buttonCriarMeta);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MetasActivity.this);
        recyclerViewMetas.setLayoutManager(layoutManager);
        recyclerViewMetas.setHasFixedSize(true);
        recyclerViewMetas.addItemDecoration(new DividerItemDecoration(MetasActivity.this, ((LinearLayoutManager) layoutManager).getOrientation()));

        editTextDataSelecionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();

                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MetasActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        editTextDataSelecionada.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        buttonCriarMeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarMeta();
            }
        });
    }

    public void criarMeta(){

        Meta meta = new Meta();
        String dataSelecionada = editTextDataSelecionada.getText().toString();
        meta.setData(dataSelecionada);
        meta.setValor(Double.parseDouble(editTextValorMeta.getText().toString()));
        meta.salvarMeta(dataSelecionada);
        Toast.makeText(getApplicationContext(), "Foi cadastrado uma nova meta!", Toast.LENGTH_SHORT).show();

    }


    //método para voltar através da seta do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void recuperarMetas (){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference referenciaTransacoes = FirebaseDatabase.getInstance().getReference();
        referenciaTransacoes.child("usuarios").child(idUsuario).child("meta").child(mesAtual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMetas.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Meta meta = dados.getValue(Meta.class);
                    listaMetas.add(meta);

                }

                MetasAdapter adapterMetas = new MetasAdapter(listaMetas);
                recyclerViewMetas.setAdapter(adapterMetas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}