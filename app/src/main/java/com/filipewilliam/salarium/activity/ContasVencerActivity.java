package com.filipewilliam.salarium.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DatasMaskWatcher;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;
import com.filipewilliam.salarium.model.Categoria;
import com.filipewilliam.salarium.model.ContasVencer;
import com.filipewilliam.salarium.viewholder.ContasVencerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ContasVencerActivity extends AppCompatActivity {

    private Spinner spinnerCategoriaContas;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Button buttonLimparCamposContasVencer, buttonCadastrarContasVencer;
    private EditText editTextValorContasVencer, editTextDataVencimentoContasVencer;
    private Switch switchEmitirNotificacaoVencimento;
    private RecyclerView recyclerViewContasVencerCadastradas;
    private DatasMaskWatcher maskWatcher;
    private FirebaseRecyclerOptions<ContasVencer> options;
    private FirebaseRecyclerAdapter<ContasVencer, ContasVencerViewHolder> adapter;

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

        referencia.child("categorias_gastos").child(idUsuario).addValueEventListener(new ValueEventListener() {
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


        referencia.child("usuarios").child("contas-a-vencer");
        options = new FirebaseRecyclerOptions.Builder<ContasVencer>().setQuery(referencia, ContasVencer.class).build();

        adapter = new FirebaseRecyclerAdapter<ContasVencer, ContasVencerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContasVencerViewHolder holder, int position, @NonNull ContasVencer model) {

                holder.textViewTipoDespesa.setText(model.getCategoria());
                holder.textViewValorDespesa.setText(String.valueOf(model.getValor()));

            }

            @NonNull
            @Override
            public ContasVencerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_ultimas_contas_a_vencer, viewGroup, false);

                return new ContasVencerViewHolder(view);
            }
        };

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewContasVencerCadastradas.setLayoutManager(layoutManager);
        recyclerViewContasVencerCadastradas.setHasFixedSize(true);
        recyclerViewContasVencerCadastradas.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        adapter.startListening();
        recyclerViewContasVencerCadastradas.setAdapter(adapter);

    }

    public void cadastrarContasVencer(){

        ContasVencer conta = new ContasVencer();
        String dataVencimento = editTextDataVencimentoContasVencer.getText().toString();
        conta.setCategoria(spinnerCategoriaContas.getSelectedItem().toString());
        conta.setDataVencimento(dataVencimento);
        conta.setValor(Double.parseDouble(String.valueOf(editTextValorContasVencer.getText())));
        conta.salvarContasAVencer(dataVencimento);
        esconderTeclado();

    }

    public void limparCampos(){

        editTextDataVencimentoContasVencer.setText(null);
        editTextValorContasVencer.setText(null);

    }

    public void esconderTeclado(){ /*garante que o teclado é ocultado, não diga, depois de clicar no botão de calcular, evitando que ele fique ativo na tela sem necessidade
        e obstruindo outros componentes*/

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch(Exception ignored) {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.startListening();

        }
    }

    @Override
    protected void onStop() {
        if(adapter != null){
            adapter.stopListening();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null){
            adapter.startListening();

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
