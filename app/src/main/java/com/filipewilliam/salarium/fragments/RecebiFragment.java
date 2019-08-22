package com.filipewilliam.salarium.fragments;


import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.Recebi;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecebiFragment extends Fragment  {

    private EditText editTextDescricao;
    private EditText editTextValor;
    private Button buttonData;
    private Spinner spinnerCategoria;
    private CheckBox checkBoxRepete;
    // text view para testes (apagar)
    private TextView textViewTestes;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private List<Recebi> recebimentos = new ArrayList<>();
    Button buttonCriarRecebimento;
    Calendar calendario;
    DatePickerDialog datePickerDialog;



    public RecebiFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_recebi, container, false);
        editTextDescricao = view.findViewById(R.id.editTextDescricao);
        editTextValor = view.findViewById(R.id.editTextValor);
        //editTextData = view.findViewById(R.id.editTextData);
        buttonData = view.findViewById(R.id.buttonData);
        spinnerCategoria = view.findViewById(R.id.spinnerCategoria);
        checkBoxRepete = view.findViewById(R.id.checkBoxRepete);
        //apagar depois
        textViewTestes = view.findViewById(R.id.textViewTeste);
        buttonCriarRecebimento = view.findViewById(R.id.buttonConfirmar);

        //selecionar data
        buttonData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    public void criarRecebimento(){


        Recebi recebimento = new Recebi();
        recebimento.setDescricao(editTextDescricao.getText().toString());
        recebimento.setValor(Double.parseDouble(String.valueOf(editTextValor.getText())));
      //  recebimento.setData(String.valueOf(editTextData.getText()));
        if (checkBoxRepete.isChecked()){
            recebimento.setRepete(true);
        } else {
            recebimento.setRepete(false);
        }

        //adicionando lista de recebimentos
       // recebimentos.add(recebimento);
        //criando novo recebimento utilizando id único
        DatabaseReference referenciaRecebimentos = referencia.child("recebimentos");
        referenciaRecebimentos.push().setValue(recebimento);
        //referencia.child("recebimento").child("001").child("Valor").setValue(recebimento.getValor());
        //textViewTestes.setText(String.valueOf(recebimento.getValor()));
    }
}
