package com.filipewilliam.salarium.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.activity.RelatoriosActivity;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosFragment extends Fragment {

    private PieChart pieChart;
    private Spinner spinnerGraficos;
    private TextView textViewAviso;
    private FloatingActionButton floatingActionButtonScreenshot;
    private DatabaseReference referencia = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private String categoriaGasto;
    private Float valorGasto;
    private Context context = getContext();

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
        textViewAviso = view.findViewById(R.id.textViewAvisoGraficos);
        floatingActionButtonScreenshot = view.findViewById(R.id.floatingActionButtonScreenShot);


        final ArrayList<PieEntry> listaDados = new ArrayList<PieEntry>();
        referencia.child("usuarios").child(idUsuario).child("transacao").orderByKey().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {
                    textViewAviso.setVisibility(View.GONE);
                    List<String> listTransacoesMeses = new ArrayList<String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        listTransacoesMeses.add(DateCustom.formatarMesAno(dataSnapshot1.getKey()));
                        Collections.reverse(listTransacoesMeses);

                        ArrayAdapter<String> transacoesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listTransacoesMeses);
                        transacoesAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                        spinnerGraficos.setAdapter(transacoesAdapter);

                    }

                } else {
                    spinnerGraficos.setVisibility(View.GONE);
                    textViewAviso.setText("Você ainda não tem um histórico de despesas!");
                    pieChart.setNoDataText("");
                    pieChart.setNoDataTextColor(android.support.design.R.color.secondary_text_default_material_dark);
                    pieChart.invalidate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        spinnerGraficos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = DateCustom.formataMesAnoFirebase((String) adapterView.getItemAtPosition(i));

                referencia.child("usuarios").child(idUsuario).child("transacao").child(item).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaDados.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                            if (dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")) {
                                categoriaGasto = String.valueOf(dataSnapshot1.child("categoria").getValue());
                                valorGasto = Float.parseFloat(String.valueOf(dataSnapshot1.child("valor").getValue()));
                                listaDados.add(new PieEntry(valorGasto, categoriaGasto));

                            }

                        }

                        if (!listaDados.isEmpty()) {
                            PieDataSet dataSet = new PieDataSet(listaDados, ""); //dataSet é o objeto que condensa os dados (valores e categoria) que usamos no gráfico
                            PieData dados = new PieData(dataSet);

                            dados.setValueFormatter(new FormatarValoresHelper()); //manda o valor bruto - 45.0 - para a classe de apoio que trata o valor e o devolve no formato correto: R$ 45.00, por exemplo
                            pieChart.setData(dados); //define que nosso gráfico pieChart usará o dataSet dados
                            dataSet.setColors(ColorTemplate.MATERIAL_COLORS); //configura as cores das fatias
                            dataSet.setSliceSpace(2f); //define uma linha branca entre as fatias com espessura de 2.0
                            dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);//define que a categoria do gasto será inscrita dentro das fatias
                            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);//define que os valores serão escritos fora
                            dataSet.setValueLinePart1OffsetPercentage(80.f);//offset da linha que aponta para o valor
                            dataSet.setValueLinePart1Length(0.4f);//comprimeto da primeira perna da linha
                            dataSet.setValueLinePart2Length(0.1f);//comprimento da segunda perna da linha

                            dados.setValueTextSize(12f); //instrui o tamanho da fonte dos textos
                            dados.setValueTextColor(Color.BLACK);//define a cor usada nos valores
                            /*Description descricao = new Description();
                            descricao.setText("Seus gastos:");
                            pieChart.setDescription(descricao);*/
                            pieChart.getDescription().setEnabled(false);
                            pieChart.getLegend().setEnabled(false);
                            pieChart.setExtraOffsets(5, 2, 5, 2);//offset do gráfico dentro do elemento pieChart do layout

                            pieChart.setDragDecelerationFrictionCoef(0.95f);//define uma inércia para o gesto de girar o gráfico
                            pieChart.animateY(2000);//tempo da animação de carregamento do gráfico
                            pieChart.setCenterText("Seus gastos em " + spinnerGraficos.getSelectedItem().toString().toLowerCase());//constroi o texto no miolo do gráfico
                            pieChart.getCenterText();

                        } else {
                            System.out.println(listaDados.isEmpty());
                            System.out.println(listaDados.size());
                            spinnerGraficos.setVisibility(View.GONE);
                            textViewAviso.setVisibility(View.VISIBLE);
                            textViewAviso.setText("Você ainda não tem um histórico de despesas!");
                            pieChart.setNoDataText("");
                            pieChart.setNoDataTextColor(android.support.design.R.color.secondary_text_default_material_dark);
                            pieChart.invalidate();
                        }

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

        floatingActionButtonScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
                    String dataHora = sdf.format(new Date());

                    String nomeArquivo = spinnerGraficos.getSelectedItem().toString().replace(" ", "_").toLowerCase() + dataHora + "_salarium.jpg";
                    pieChart.saveToGallery(nomeArquivo, 100);

                    Toast.makeText(getActivity(), "Imagem salva com sucesso na sua galeria!", Toast.LENGTH_SHORT).show();

                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }

        });
        return view;
    }

}