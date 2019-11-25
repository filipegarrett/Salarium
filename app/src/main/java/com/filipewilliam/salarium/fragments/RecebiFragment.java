package com.filipewilliam.salarium.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.CategoriasActivity;
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
public class RecebiFragment extends Fragment {

    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private EditText editTextDescricaoRecebimento;
    private EditText editTextValorRecebimento;
    private EditText editTextDataSelecionadaRecebimento;
    private Spinner spinnerCategoriaRecebimento;
    private Button buttonCriarRecebimento, buttonLimparCamposRecebimento, buttonCriarCategoriaRecebimento;

    public RecebiFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recebi, container, false);

        String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        editTextDescricaoRecebimento = view.findViewById(R.id.editTextDescricaoRecebimento);
        editTextValorRecebimento = view.findViewById(R.id.editTextValorRecebimento);
        editTextValorRecebimento.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorRecebimento));
        editTextDataSelecionadaRecebimento = view.findViewById(R.id.editTextDataRecebimento);
        spinnerCategoriaRecebimento = view.findViewById(R.id.spinnerCategoriaRecebimento);
        buttonCriarRecebimento = view.findViewById(R.id.buttonConfirmarRecebimento);
        buttonLimparCamposRecebimento = view.findViewById(R.id.buttonLimparCamposRecebimentos);
        buttonCriarCategoriaRecebimento = view.findViewById(R.id.buttonCriarCategoriaRecebido);

        referencia.child("usuarios").child(idUsuario).child("categorias_recebimentos").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            List<String> listCategorias = new ArrayList<String>();
            for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                Categoria nomeCategoria = categoriaSnapshot.getValue(Categoria.class);
                listCategorias.add(nomeCategoria.getDescricaoCategoria());

                ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listCategorias);
                categoriasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                spinnerCategoriaRecebimento.setAdapter(categoriasAdapter);
            }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            //selecionar data
        editTextDataSelecionadaRecebimento.setOnClickListener(new View.OnClickListener() {
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
                        editTextDataSelecionadaRecebimento.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        buttonCriarCategoriaRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CategoriasActivity.class);
                intent.putExtra("TIPO", "recebido");
                startActivity(intent);
            }
        });

        //criar recebimento
        buttonCriarRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarRecebimento();
            }
        });

        buttonLimparCamposRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparCamposRecebimento();
            }
        });

        return view;

    }

    public void criarRecebimento() {

        if (validarCamposRecebimentos() == true) {

            Transacao transacao = new Transacao();
            Double recebimentoPreenchido = Double.parseDouble(editTextValorRecebimento.getText().toString().replace(",", ""));
            String dataRecebimento = editTextDataSelecionadaRecebimento.getText().toString();
            transacao.setDescricao(editTextDescricaoRecebimento.getText().toString());
            transacao.setValor(recebimentoPreenchido);
            transacao.setData(dataRecebimento);
            transacao.setCategoria( spinnerCategoriaRecebimento.getSelectedItem().toString());
            transacao.setTipo("Recebi");
            transacao.salvarTransacao(dataRecebimento);
            Toast.makeText(getContext(), "Valor cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

        }
    }

    public boolean validarCamposRecebimentos() {

        String descricao = editTextDescricaoRecebimento.getText().toString();
        String valor = editTextValorRecebimento.getText().toString();
        String data = editTextDataSelecionadaRecebimento.getText().toString();

        if (!descricao.isEmpty()) {
            if (!valor.isEmpty()) {
                if (!data.isEmpty()) {
                    if (spinnerCategoriaRecebimento != null && spinnerCategoriaRecebimento.getSelectedItem()!= null){

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

    public void limparCamposRecebimento (){
        editTextDescricaoRecebimento.setText("");
        editTextValorRecebimento.setText("");
        editTextDataSelecionadaRecebimento.setText("");
    }

}
