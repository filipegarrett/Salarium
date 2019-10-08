package com.filipewilliam.salarium.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.RelatoriosAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.model.Transacao;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosFragment extends Fragment {

    private PieChart pieChart;
    private Spinner spinnerGraficos;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DateCustom dateCustom;
    private String categoriaGasto;
    private Float valorGasto;

    public GraficosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graficos, container, false);
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        pieChart = view.findViewById(R.id.pieChartRelatorio);
        spinnerGraficos = view.findViewById(R.id.spinnerGraficos);

        final ArrayList<PieEntry> listaDados = new ArrayList<PieEntry>();
        final int i = 0;
        final String mesAno = "";
        referencia.child("usuarios").child(idUsuario).child("transacao").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    List<String> listTransacoesMeses = new ArrayList<String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        listTransacoesMeses.add(dateCustom.formatarMesAno(dataSnapshot1.getKey()));

                        ArrayAdapter<String> transacoesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listTransacoesMeses);
                        transacoesAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                        spinnerGraficos.setAdapter(transacoesAdapter);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        spinnerGraficos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = dateCustom.formataMesAnoFirebase((String) adapterView.getItemAtPosition(i));

                referencia.child("usuarios").child(idUsuario).child("transacao").child(item).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaDados.clear();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            if(dataSnapshot1.child("tipo").getValue().toString().equals("Gasto")){
                                categoriaGasto = String.valueOf(dataSnapshot1.child("categoria").getValue());
                                valorGasto = Float.parseFloat(String.valueOf(dataSnapshot1.child("valor").getValue()));
                                listaDados.add(new PieEntry(valorGasto, categoriaGasto));

                            }

                        }

                        PieDataSet dataSet = new PieDataSet(listaDados, "");
                        PieData dados = new PieData(dataSet);

                        dados.setValueFormatter(new PercentFormatter());
                        pieChart.setData(dados);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dados.setValueTextSize(13f);
                        dados.setValueTextColor(Color.WHITE);
                        Description descricao = new Description();
                        descricao.setText("Seus gastos:");
                        pieChart.setDescription(descricao);
                        pieChart.animateX(3000);
                        pieChart.setCenterText(spinnerGraficos.getSelectedItem().toString());
                        pieChart.getCenterText();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return view;
    }

}
