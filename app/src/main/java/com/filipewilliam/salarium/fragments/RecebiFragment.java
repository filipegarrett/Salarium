package com.filipewilliam.salarium.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.Categoria;
import com.filipewilliam.salarium.model.Recebi;
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

    private EditText editTextDescricao;
    private EditText editTextValor;
    private EditText editTextDataSelecionada;
    private Spinner spinnerCategoria;
    private CheckBox checkBoxRepete;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    //private List<Recebi> recebimentos = new ArrayList<>();
    private Button buttonCriarCategoria;
    Button buttonCriarRecebimento;

    public RecebiFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recebi, container, false);

        editTextDescricao = view.findViewById(R.id.editTextDescricao);
        editTextValor = view.findViewById(R.id.editTextValor);
        editTextDataSelecionada = view.findViewById(R.id.editTextDataSelecionada);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        checkBoxRepete = view.findViewById(R.id.checkBoxRepete);
        buttonCriarRecebimento = view.findViewById(R.id.buttonConfirmarExcluirUsuario);
        buttonCriarCategoria = view.findViewById(R.id.buttonCriarCategoria);
        //referencia de categorias no firebase
       // DatabaseReference referenciaCategorias = referencia.child("categorias");

        referencia.child("categorias").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            List<String> listCategorias = new ArrayList<String>();
            for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                Categoria nomeCategoria = categoriaSnapshot.getValue(Categoria.class);
                listCategorias.add(nomeCategoria.getDescricaoCategoria());

                ArrayAdapter<String> categoriasAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listCategorias);
                categoriasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                spinnerCategoria.setAdapter(categoriasAdapter);
                }
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

            //selecionar data
        editTextDataSelecionada.setOnClickListener(new View.OnClickListener() {
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
                        editTextDataSelecionada.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //criar categoria
        buttonCriarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarCategoria();
            }
        });

        //criar recebimento
        buttonCriarRecebimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                criarRecebimento();
            }
        });

        return view;


    }

    public void criarRecebimento() {

        if (validarCamposRecebimentos() == true) {

            Recebi recebimento = new Recebi();
            recebimento.setDescricao(editTextDescricao.getText().toString());
            recebimento.setValor(Double.parseDouble(String.valueOf(editTextValor.getText())));
            recebimento.setData(String.valueOf(editTextDataSelecionada.getText()));
            if (checkBoxRepete.isChecked()) {
                recebimento.setRepete(true);
            } else {
                recebimento.setRepete(false);
            }
            recebimento.setDescricaoCategoria( spinnerCategoria.getSelectedItem().toString());
            ;
            //criando novo recebimento utilizando id único
            DatabaseReference referenciaRecebimentos = referencia.child("recebimentos");
            referenciaRecebimentos.push().setValue(recebimento);

        }
    }


    public boolean validarCamposRecebimentos() {
        String descricao = editTextDescricao.getText().toString();
        String valor = editTextValor.getText().toString();
        String data = editTextDataSelecionada.getText().toString();

        if (!descricao.isEmpty()) {
            if (!valor.isEmpty()) {
                if (!data.isEmpty()) {
                } else {
                    Toast.makeText(getContext(), "Data não foi preenchida", Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                Toast.makeText(getContext(), "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getContext(), "Descrição não foi preenchida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    public void criarCategoria (){

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("Criar Categoria");
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
                DatabaseReference referenciaCategorias = referencia.child("categorias");
                referenciaCategorias.push().setValue(novaCategoria);
            }
        });

        dialog.create();
        dialog.show();
    }

}
