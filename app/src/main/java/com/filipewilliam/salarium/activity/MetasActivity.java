package com.filipewilliam.salarium.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.Meta;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MetasActivity extends AppCompatActivity {

    private Spinner spinnerMetas;
    private Button buttonSalvarOuAlterarMetas;
    private Button buttonExcluirMetas;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private EditText editTextDataMeta;
    private EditText editTextValorMetas;
    private TextView textViewValorMetas;
    private TextView textViewTotalGastoMetas;
    private TextView textViewSaldoMetas;
    private String mesAtual = DateCustom.retornaMesAno();
    private Double gastoMes = 0.0;
    private Double valorMeta = 0.0;
    private List<String> listMetasMeses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Metas");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);
        spinnerMetas = findViewById(R.id.spinnerMesMetas);
        editTextValorMetas = findViewById(R.id.editTextValorMetas);
        editTextDataMeta = findViewById(R.id.editTextDataMeta);
        buttonSalvarOuAlterarMetas = findViewById(R.id.buttonSalvarOuAlterarMetas);
        buttonExcluirMetas = findViewById(R.id.buttonExcluirMetas);
        textViewSaldoMetas = findViewById(R.id.textViewSaldoMetas);
        textViewTotalGastoMetas = findViewById(R.id.textViewGastoMes);
        textViewValorMetas = findViewById(R.id.textViewValorMetas);
        recuperarGastoMes(mesAtual);
        recuperarValorMeta(mesAtual);

        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        referencia.child("usuarios").child(idUsuario).child("meta")/*.orderByKey()*/.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    listMetasMeses.add(DateCustom.formatarMesAno(dataSnapshot1.getKey()));
                    ArrayAdapter<String> metasAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, listMetasMeses);
                    metasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                    spinnerMetas.setAdapter(metasAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //selecionar data da meta através de um datepicker
        editTextDataMeta.setOnClickListener(new View.OnClickListener() {
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
                        editTextDataMeta.setText(month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        //listener para atualizar o resumo da tela de metas
        spinnerMetas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = DateCustom.formataMesAnoFirebase((String) parent.getItemAtPosition(position));
                System.out.println(position);
                Double metaAtual = recuperarValorMeta(item);
                Double gastoAtual = recuperarGastoMes(item);
                Double saldoTotal = (metaAtual - gastoAtual);
                textViewTotalGastoMetas.setText(String.valueOf(gastoAtual));
                textViewValorMetas.setText(String.valueOf(metaAtual));
                textViewSaldoMetas.setText(String.valueOf(saldoTotal));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //listener para salvar uma nova meta ou alterar caso já exista uma no mesmo mês (método alterar é chamado de dentro do método criarMeta())
        buttonSalvarOuAlterarMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarMeta();

            }
        });

        //listener para exclusão da meta selecionada no spinner
        buttonExcluirMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excluirMeta();
            }
        });


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


    //cria uma nova meta caso não tenha nó filho ou chama o alterar caso exista
    public void criarMeta() {

        if (validarCamposMetas()) {

            FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            DatabaseReference referenciaMeta = FirebaseDatabase.getInstance().getReference();
            String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
            final String dataSelecionada = DateCustom.formatarData(editTextDataMeta.getText().toString());

            referenciaMeta.child("usuarios").child(idUsuario).child("meta").child(dataSelecionada).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        alterarMeta();
                    } else {
                        Meta meta = new Meta();
                        meta.setData(dataSelecionada);
                        meta.setValor(Double.parseDouble(editTextValorMetas.getText().toString()));
                        meta.salvarMetaFirebase(dataSelecionada);
                        Toast.makeText(getApplicationContext(), "Meta salva com sucesso!", Toast.LENGTH_SHORT).show();
                        listMetasMeses.clear();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }

    }

    //método chamado caso o mês selecionado tenha nós filhos
    private void alterarMeta() {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        final DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        final String dataSelecionada = DateCustom.formatarData(editTextDataMeta.getText().toString());
        final Meta meta = new Meta();
        Double valor = Double.parseDouble(editTextValorMetas.getText().toString());
        meta.setData(dataSelecionada);
        meta.setValor(valor);
        referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(dataSelecionada).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot metaSnapshot : dataSnapshot.getChildren()) {
                    final String key = metaSnapshot.getKey();
                    referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(dataSelecionada).child(key).setValue(meta);
                    listMetasMeses.clear();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(), "Foi alterado a meta do mês selecionado!", Toast.LENGTH_SHORT).show();
    }

    //método para excluir a meta que foi selecionada no spinner
    public void excluirMeta() {
        String dataSelecionada = DateCustom.formataMesAnoFirebase(spinnerMetas.getSelectedItem().toString());
        Meta.excluirMetaFirebase(dataSelecionada);
        textViewSaldoMetas.setText("");
        textViewValorMetas.setText("");
        Toast.makeText(getApplicationContext(), "A Meta foi excluída com sucesso!", Toast.LENGTH_SHORT).show();
        listMetasMeses.clear();
    }

    //verifica se os campos da meta foram preenchidos e impede cadastro caso estejam sem valor
    public boolean validarCamposMetas() {

        if (!editTextValorMetas.getText().toString().isEmpty()) {
            if(!editTextDataMeta.getText().toString().isEmpty()){
            }else{
                Toast.makeText(getApplicationContext(), "A data da Meta não foi preenchida", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "O valor da Meta não foi preenchido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    //método para recuperar o gasto do Mes selecionado
    public Double recuperarGastoMes(String item) {
        System.out.println("Item gasto do mes " +item);
        final ArrayList<Transacao> listaTransacoes = new ArrayList<>();
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        referencia.child("usuarios").child(idUsuario).child("transacao").child(item)/*.orderByChild("data")*/.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaTransacoes.clear();
                double totalDespesaMes = 0.0;

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Transacao transacao = dataSnapshot1.getValue(Transacao.class);
                    listaTransacoes.add(transacao);

                    if (dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")) {
                        totalDespesaMes = totalDespesaMes + (transacao.getValor());
                        gastoMes = totalDespesaMes;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return gastoMes;
    }

    //recuperar valor da meta
    public Double recuperarValorMeta(String item) {
        System.out.println("Item meta do mes " +item);
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        referencia.child("usuarios").child(idUsuario).child("meta").child(item).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                    Meta meta = dataSnapshot2.getValue(Meta.class);
                    valorMeta = meta.getValor();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return valorMeta;
    }

}

