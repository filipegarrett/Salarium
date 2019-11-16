package com.filipewilliam.salarium.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;
import com.filipewilliam.salarium.model.Categoria;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GasteiFragment extends Fragment {

    private EditText editTextDescricaoGasto;
    private EditText editTextValorGasto;
    private EditText editTextDataSelecionadaGasto;
    private Spinner spinnerCategoriaGasto;
    private Button buttonCriarGasto;
    private Button buttonLimparCamposGasto;
    private Button buttonCriarCategoriaGasto;
    private FloatingActionButton fabAdicionarCategoriaGasto;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    public GasteiFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gastei, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        editTextDescricaoGasto = view.findViewById(R.id.editTextDescricaoGasto);
        editTextValorGasto = view.findViewById(R.id.editTextValorGasto);
        editTextValorGasto.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorGasto));
        editTextDataSelecionadaGasto = view.findViewById(R.id.editTextDataGasto);
        spinnerCategoriaGasto = view.findViewById(R.id.spinnerCategoriaGasto);
        buttonCriarGasto = view.findViewById(R.id.buttonConfirmarGasto);
        buttonLimparCamposGasto = view.findViewById(R.id.buttonLimparCamposGasto);
        buttonCriarCategoriaGasto = view.findViewById(R.id.buttonCriarCategoriaGasto);

        referencia.child("usuarios").child(idUsuario).child("categorias_gastos").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> listCategorias = new ArrayList<>();
                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    Categoria nomeCategoria = categoriaSnapshot.getValue(Categoria.class);
                    listCategorias.add(nomeCategoria.getDescricaoCategoria());

                    ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listCategorias);
                    categoriasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                    spinnerCategoriaGasto.setAdapter(categoriasAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //selecionar data
        editTextDataSelecionadaGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();

                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        editTextDataSelecionadaGasto.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        buttonCriarCategoriaGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                criarCategoriaGasto();
            }
        });

        //criar gasto
        buttonCriarGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarGasto();
            }
        });

        buttonLimparCamposGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparCamposGasto();
            }
        });

        return view;

    }

    public void criarGasto() {

        if (validarCamposGastos() == true) {

            Transacao transacao = new Transacao();
            Double gastoPreenchido = Double.parseDouble(editTextValorGasto.getText().toString().replace(",", ""));
            String dataGasto = editTextDataSelecionadaGasto.getText().toString();
            transacao.setDescricao(editTextDescricaoGasto.getText().toString());
            transacao.setValor(gastoPreenchido);
            transacao.setData(dataGasto);
            transacao.setCategoria(spinnerCategoriaGasto.getSelectedItem().toString());
            transacao.setTipo("Gastei");
            transacao.salvarTransacao(dataGasto);
            Toast.makeText(getContext(), "Valor cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

        }
    }


    public boolean validarCamposGastos() {

        String descricao = editTextDescricaoGasto.getText().toString();
        String valor = editTextValorGasto.getText().toString();
        String data = editTextDataSelecionadaGasto.getText().toString();

        if (!descricao.isEmpty()) {
            if (!valor.isEmpty()) {
                if (!data.isEmpty()) {
                    //verifica se foi criado ao menos uma categoria para poder cadastrar
                    if (spinnerCategoriaGasto != null && spinnerCategoriaGasto.getSelectedItem()!= null){
                    } else{
                        Toast.makeText(getContext(), "Você precisa criar uma categoria antes!", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(getContext(), "Você precisa escolher uma data", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getContext(), "Você precisa precisa definir um valor", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "Você precisa descrever o gasto", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    public void criarCategoriaGasto() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Criar nova categoria");
        dialog.setCancelable(true);
        //necessário estes parâmetros pois somente o edittext não aparecia.
        final EditText categoria = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        categoria.setLayoutParams(lp);
        dialog.setView(categoria);


        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialog.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Categoria novaCategoria = new Categoria();
                novaCategoria.setDescricaoCategoria(categoria.getText().toString());
                novaCategoria.salvarCategoriaGasto();
                Toast.makeText(getContext(), "Categoria criada com sucesso!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.create();
        dialog.show();
    }

    public void limparCamposGasto (){

        editTextDescricaoGasto.setText("");
        editTextValorGasto.setText("");
        editTextDataSelecionadaGasto.setText("");
    }


}