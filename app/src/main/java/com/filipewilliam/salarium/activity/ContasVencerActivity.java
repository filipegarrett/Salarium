package com.filipewilliam.salarium.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.ContasVencerAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DatasMaskWatcher;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.DeslizarApagarCallback;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;
import com.filipewilliam.salarium.model.Categoria;
import com.filipewilliam.salarium.model.ContasVencer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ContasVencerActivity extends AppCompatActivity {

    private Spinner spinnerCategoriaContas;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Button buttonLimparCamposContasVencer, buttonCadastrarContasVencer;
    private EditText editTextValorContasVencer, editTextDataVencimentoContasVencer;
    private Switch switchEmitirNotificacaoVencimento;
    private ProgressBar progressBar;
    private TextView textViewSemContaCadastrada;
    private RecyclerView recyclerViewContasVencerCadastradas;
    private DatasMaskWatcher maskWatcher;
    private DateCustom dateCustom;
    private ArrayList<ContasVencer> listaContasVencer;
    private ArrayList<String> keys = new ArrayList<>();
    private String keysMes;
    private ContasVencerAdapter adapter;
    private final Date hoje = dateCustom.retornaDataHojeDateFormat();
    private final SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Próximas despesas");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_vencer);
        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        spinnerCategoriaContas = findViewById(R.id.spinnerCategoriaContasVencer);
        editTextValorContasVencer = findViewById(R.id.editTextValorContasVencer);
        editTextValorContasVencer.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorContasVencer));
        editTextDataVencimentoContasVencer = findViewById(R.id.editTextDataVencimentoContaVencer);
        buttonCadastrarContasVencer = findViewById(R.id.buttonCadastrarContasVencer);
        buttonLimparCamposContasVencer = findViewById(R.id.buttonLimparCamposContasVencer);
        switchEmitirNotificacaoVencimento = findViewById(R.id.switchPermitirNotificacoes);
        progressBar = findViewById(R.id.progressBarRecyclerAtualiza);
        textViewSemContaCadastrada = findViewById(R.id.textViewAvisoSemContaVencerCadastrada);
        recyclerViewContasVencerCadastradas = findViewById(R.id.recyclerViewContasVencer);

        editTextDataVencimentoContasVencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();

                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(ContasVencerActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        editTextDataVencimentoContasVencer.setText(dayOfMonth + "/" + month + "/" + year);

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        referencia.child("usuarios").child(idUsuario).child("categorias_gastos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> listCategorias = new ArrayList<String>();
                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    Categoria nomeCategoria = categoriaSnapshot.getValue(Categoria.class);
                    listCategorias.add(nomeCategoria.getDescricaoCategoria());

                    ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listCategorias);
                    categoriasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                    spinnerCategoriaContas.setAdapter(categoriasAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

        });

        buttonCadastrarContasVencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!spinnerCategoriaContas.getSelectedItem().toString().isEmpty()){
                    if(!editTextDataVencimentoContasVencer.getText().toString().isEmpty()){
                        if(!editTextValorContasVencer.getText().toString().isEmpty()){
                            cadastrarContasVencer();
                            Toast.makeText(getApplicationContext(), "Despesa salva com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos();

                        }else{
                            Toast.makeText(ContasVencerActivity.this, "Você precisa definir um valor para a despesa!", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(ContasVencerActivity.this, "Você precisa escolher uma data antes!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(ContasVencerActivity.this, "Você precisa escolher uma categoria antes!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonLimparCamposContasVencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limparCampos();
            }
        });

        recyclerViewContasVencerCadastradas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContasVencerCadastradas.setHasFixedSize(true);
        listaContasVencer = new ArrayList<ContasVencer>();

        final DatabaseReference referencia2 = FirebaseDatabase.getInstance().getReference();
        referencia2.child("usuarios").child(idUsuario).child("contas-a-vencer").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaContasVencer.clear();

                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                    for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                        ContasVencer conta = dataSnapshot2.getValue(ContasVencer.class);
                        System.out.println(dataSnapshot2.child("dataVencimento").getChildrenCount());
                        System.out.println(dataSnapshot2);
                        try {
                            if(data.parse(dataSnapshot2.child("dataVencimento").getValue().toString()).compareTo(hoje) >= 0){
                                System.out.println(dataSnapshot2.child("dataVencimento").getChildrenCount());
                                System.out.println(dataSnapshot2);

                                if(dataSnapshot2.exists()){
                                    progressBar.setVisibility(View.GONE);
                                    keys.add(dataSnapshot2.getKey());
                                    keysMes = dataSnapshot1.getKey();
                                    listaContasVencer.add(conta);

                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    textViewSemContaCadastrada.setText("Você não tem nenhuma despesa para pagar =)");

                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

                adapter = new ContasVencerAdapter(ContasVencerActivity.this, listaContasVencer, keys, keysMes);
                recyclerViewContasVencerCadastradas.setAdapter(adapter);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarApagarCallback(adapter));
                itemTouchHelper.attachToRecyclerView(recyclerViewContasVencerCadastradas);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ContasVencerActivity.this, "Opsss, algo deu errado =/", Toast.LENGTH_SHORT).show();

            }
        });


    }

    public void cadastrarContasVencer(){

        ContasVencer conta = new ContasVencer();
        String dataVencimento = editTextDataVencimentoContasVencer.getText().toString();
        conta.setCategoria(spinnerCategoriaContas.getSelectedItem().toString());
        conta.setDataVencimento(dataVencimento);
        conta.setValor(Double.parseDouble(editTextValorContasVencer.getText().toString().replace(",","")));
        conta.salvarContasAVencer(dataVencimento);
        esconderTeclado();

    }

    public void limparCampos(){

        editTextDataVencimentoContasVencer.setText(null);
        editTextValorContasVencer.setText(null);

    }

    public void esconderTeclado(){
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch(Exception ignored) {
        }

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
