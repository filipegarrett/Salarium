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
    private DateCustom dateCustom;
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

                if(dataSnapshot.getChildrenCount() > 0){
                    textViewAviso.setVisibility(View.GONE);
                    List<String> listTransacoesMeses = new ArrayList<String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        listTransacoesMeses.add(dateCustom.formatarMesAno(dataSnapshot1.getKey()));
                        Collections.reverse(listTransacoesMeses);

                        ArrayAdapter<String> transacoesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listTransacoesMeses);
                        transacoesAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                        spinnerGraficos.setAdapter(transacoesAdapter);

                    }

                }else {
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
                String item = dateCustom.formataMesAnoFirebase((String) adapterView.getItemAtPosition(i));

                referencia.child("usuarios").child(idUsuario).child("transacao").child(item).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listaDados.clear();
                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){

                            if(dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")){
                                categoriaGasto = String.valueOf(dataSnapshot1.child("categoria").getValue());
                                valorGasto = Float.parseFloat(String.valueOf(dataSnapshot1.child("valor").getValue()));
                                listaDados.add(new PieEntry(valorGasto, categoriaGasto));

                            }

                        }

                        PieDataSet dataSet = new PieDataSet(listaDados, "");
                        PieData dados = new PieData(dataSet);

                        dados.setValueFormatter(new FormatarValoresHelper());
                        pieChart.setData(dados);
                        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        dados.setValueTextSize(10f);
                        dados.setValueTextColor(Color.WHITE);
                        /*Description descricao = new Description();
                        descricao.setText("Seus gastos:");
                        pieChart.setDescription(descricao);*/
                        pieChart.getDescription().setEnabled(false);
                        pieChart.getLegend().setEnabled(false);
                        pieChart.animateY(3000);
                        pieChart.setCenterText("Seus gastos em " + spinnerGraficos.getSelectedItem().toString().toLowerCase());
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

        floatingActionButtonScreenshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                    SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
                    String dataHora = sdf.format(new Date());

                    String nomeArquivo = spinnerGraficos.getSelectedItem().toString().replace(" ", "_").toLowerCase() + dataHora + "_salarium.jpg";
                    pieChart.saveToGallery(nomeArquivo, 100);

                    Toast.makeText(getActivity(), "Imagem salva com sucesso na sua galeria!", Toast.LENGTH_SHORT).show();

                }else{
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }

        });
        return view;
    }

}

    /*final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
