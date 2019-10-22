package com.filipewilliam.salarium.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.model.Meta;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetasActivity extends AppCompatActivity {

    private Spinner spinnerMetas;
    private Button buttonSalvarOuAlterarMetas;
    private Button buttonExcluirMetas;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DateCustom dateCustom;
    private EditText editTextValorMetas;
    private TextView textViewValorMetas;
    private TextView textViewTotalGastoMetas;
    private TextView textViewSaldoMetas;
    private String mesAtual = dateCustom.retornaMesAno();
    private Double gastoMes = 0.0;
    private Double valorMeta = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Metas:");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_metas);
        spinnerMetas = findViewById(R.id.spinnerMesMetas);
        editTextValorMetas = findViewById(R.id.editTextValorMetas);
        buttonSalvarOuAlterarMetas = findViewById(R.id.buttonSalvarOuAlterarMetas);
        buttonExcluirMetas = findViewById(R.id.buttonExcluirMetas);
        textViewSaldoMetas = findViewById(R.id.textViewSaldoMetas);
        textViewTotalGastoMetas = findViewById(R.id.textViewTotalGastoMetas);
        textViewValorMetas = findViewById(R.id.textViewValorMetas);

        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        final List<String> listMetasMeses = new ArrayList<>();

        referencia.child("usuarios").child(idUsuario).child("meta").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        listMetasMeses.clear(); //removeu problema de dados duplicados no spinner
                        listMetasMeses.add(dateCustom.formatarMesAno(dataSnapshot1.getKey()));
                        Collections.reverse(listMetasMeses); //Não é muito elegante, mas o Firebase não conhece o conceito de ordenar dados de forma decrescente...
                        ArrayAdapter<String> metasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listMetasMeses);
                        metasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                        spinnerMetas.setAdapter(metasAdapter);
                        //hasChild para corrigir problema do mês seguinte

                    }
                } else {
                    //caso não possua metas lançadas entra nesta condição para criar uma com o mês atual
                    listMetasMeses.clear();
                    String mesAnoFirebase = dateCustom.retornaMesAno(); // 102019
                    mesAnoFirebase = dateCustom.formatarMesAno(mesAnoFirebase);  //método para retornar mês descrito ex: Outubro 2019
                    listMetasMeses.add(mesAnoFirebase);
                    ArrayAdapter<String> metasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listMetasMeses);
                    metasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                    spinnerMetas.setAdapter(metasAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        //recuperar valor de gastos do mês atual
        final ArrayList<Transacao> listaTransacoes = new ArrayList<>();
        referencia.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTransacoes.clear();
                for (DataSnapshot gastoSnapshot: dataSnapshot.getChildren()) {
                    Transacao transacao = gastoSnapshot.getValue(Transacao.class);
                    //gastoMes = transacao.getValor();
                    listaTransacoes.add(transacao);
                    if (gastoSnapshot.child("tipo").getValue().toString().equals("Gastei")) {
                        gastoMes = gastoMes + Double.valueOf(transacao.getValor());
                    }
                }

                textViewTotalGastoMetas.setText(gastoMes.toString());
                verificarMeta(gastoMes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //recuperar valor da meta
            spinnerMetas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    String item = dateCustom.formataMesAnoFirebase((String) adapterView.getItemAtPosition(position));
                    // System.out.println(item);

                    referencia.child("usuarios").child(idUsuario).child("meta").child(item).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot2 : dataSnapshot.getChildren()) {
                                Meta meta = dataSnapshot2.getValue(Meta.class);
                                valorMeta = meta.getValor();
                                //System.out.println(valorMeta);
                                textViewValorMetas.setText(String.valueOf(valorMeta));
                                verificarMeta(valorMeta);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });






        buttonSalvarOuAlterarMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarMeta();
                //listMetasMeses.clear();

            }
        });

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


    public void criarMeta() {

        if (validarCamposMetas()) {

            FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            DatabaseReference referenciaMeta = FirebaseDatabase.getInstance().getReference();
            String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
            final String dataSelecionada = DateCustom.formataMesAnoFirebase(spinnerMetas.getSelectedItem().toString());

            referenciaMeta.child("usuarios").child(idUsuario).child("meta").child(DateCustom.retornaMesAno()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChildren()) {
                        alterarMeta();
                        //Toast.makeText(getApplicationContext(), "Já existe uma meta neste mês", Toast.LENGTH_SHORT).show();
                    } else {
                        Meta meta = new Meta();
                        meta.setData(dataSelecionada);
                        meta.setValor(Double.parseDouble(editTextValorMetas.getText().toString()));
                        meta.salvarMetaFirebase(dataSelecionada);
                        Toast.makeText(getApplicationContext(), "Foi cadastrado uma nova meta!", Toast.LENGTH_SHORT).show();

                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }

    }


    private void alterarMeta() {
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        final DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebaseDatabase();
        final String dataSelecionada = DateCustom.formataMesAnoFirebase(spinnerMetas.getSelectedItem().toString());
        final Meta meta = new Meta();
        Double valor = Double.parseDouble(editTextValorMetas.getText().toString());
        meta.setData(dataSelecionada);
        meta.setValor(valor);
        referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(dataSelecionada).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot metaSnapshot: dataSnapshot.getChildren()){
                    final String key = metaSnapshot.getKey();
                    referenciaFirebase.child("usuarios").child(idUsuario).child("meta").child(dataSelecionada).child(key).setValue(meta);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(getApplicationContext(), "Foi alterado a meta do mês selecionado!", Toast.LENGTH_SHORT).show();
    }

    public void excluirMeta(){
        String dataSelecionada = DateCustom.formataMesAnoFirebase(spinnerMetas.getSelectedItem().toString());
        Meta.excluirMetaFirebase(dataSelecionada);
        textViewSaldoMetas.setText("");
        textViewValorMetas.setText("");
        Toast.makeText(getApplicationContext(), "A Meta foi excluída com sucesso!", Toast.LENGTH_SHORT).show();

    }

    public void verificarMeta(Double meta){

        Double saldoTotal = 0.0;
        saldoTotal = (meta - gastoMes);
        textViewSaldoMetas.setText(String.valueOf(saldoTotal));

    }

    public boolean validarCamposMetas() {

        if (!editTextValorMetas.getText().toString().isEmpty()) {
            return true;
        }
        Toast.makeText(getApplicationContext(), "O valor da Meta não foi preenchido", Toast.LENGTH_SHORT).show();
        return false;


    }

}