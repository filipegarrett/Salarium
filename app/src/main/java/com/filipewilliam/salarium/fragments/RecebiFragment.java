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
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.Recebi;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecebiFragment extends Fragment  {

    private EditText editTextDescricao;
    private EditText editTextValor;
    private EditText editTextDataSelecionada;
    private Spinner spinnerCategoria;
    private CheckBox checkBoxRepete;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private List<Recebi> recebimentos = new ArrayList<>();
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
        buttonCriarRecebimento = view.findViewById(R.id.buttonConfirmar);
        //selecionar data
        editTextDataSelecionada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();

                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
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

        if (validarCamposRecebimentos()==true){

            Recebi recebimento = new Recebi();
            recebimento.setDescricao(editTextDescricao.getText().toString());
            recebimento.setValor(Double.parseDouble(String.valueOf(editTextValor.getText())));
            recebimento.setData(String.valueOf(editTextDataSelecionada.getText()));
            if (checkBoxRepete.isChecked()){
                recebimento.setRepete(true);
            } else {
                recebimento.setRepete(false);
            }
;
            //criando novo recebimento utilizando id único
            DatabaseReference referenciaRecebimentos = referencia.child("recebimentos");
            referenciaRecebimentos.push().setValue(recebimento);
            //referencia.child("recebimento").child("001").child("Valor").setValue(recebimento.getValor());
        }
    }


        public boolean validarCamposRecebimentos(){
          String descricao = editTextDescricao.getText().toString();
          String valor = editTextValor.getText().toString();
          String data = editTextDataSelecionada.getText().toString();

          if (!descricao.isEmpty()){
                if (!valor.isEmpty()){
                    if (!data.isEmpty()) {
                    }else {
                            Toast.makeText(getContext(), "Data não foi preenchida", Toast.LENGTH_SHORT).show();
                            return false;
                    }

                }else {
                    Toast.makeText(getContext(), "Valor não foi preenchido", Toast.LENGTH_SHORT).show();
                    return false;
                }
          } else {
              Toast.makeText(getContext(), "Descrição não foi preenchida", Toast.LENGTH_SHORT).show();
              return false;
          }

          return true;

        }


}
