package com.filipewilliam.salarium.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.ResumoAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.DeslizarApagarCallback;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.Meta;
import com.filipewilliam.salarium.model.Transacao;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {

    double valorMetas = 0;
    double gastoTotal;
    private String mesAtual = DateCustom.retornaMesAno();
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<Transacao> listaTransacoes = new ArrayList<>();
    private TextView textViewTotalGasto, textViewTotalRecebido, textViewValorSaldo, textViewNadaARelatarResumo, textViewSemDadosGrafico;
    private RecyclerView recyclerViewTransacoes;
    private ProgressBar progressBarResumo, progressBarGrafico;
    private PieChart pieChartResumo;
    private DatabaseReference referenciaMetas = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener valueEventListenerResumo;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recuperarTransacoes();
        //recuperarResumo();
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);
        textViewValorSaldo = view.findViewById(R.id.textViewValorSaldo);
        textViewTotalRecebido = view.findViewById(R.id.textViewTotalRecebido);
        textViewTotalGasto = view.findViewById(R.id.textViewTotalGasto);
        textViewNadaARelatarResumo = view.findViewById(R.id.textViewNadaARelatarResumo);
        textViewSemDadosGrafico = view.findViewById(R.id.textViewAvisoSemDadosGraficoResumo);
        progressBarResumo = view.findViewById(R.id.progressBarResumo);
        progressBarResumo.setVisibility(View.VISIBLE);
        pieChartResumo = view.findViewById(R.id.pieChartResumo);
        pieChartResumo.setNoDataText("Você ainda não definiu uma meta para o mês");
        progressBarGrafico = view.findViewById(R.id.progressBarGraficoResumo);
        progressBarGrafico.setVisibility(View.INVISIBLE);
        recyclerViewTransacoes = view.findViewById(R.id.recyclerViewTransacoes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTransacoes.setLayoutManager(layoutManager);
        recyclerViewTransacoes.setHasFixedSize(true);

        return view;

    }

    //recupera as transações do mês atual e preenche na recycler view
    public void recuperarTransacoes() {

        DatabaseReference referenciaTransacoes = FirebaseDatabase.getInstance().getReference();
        referenciaTransacoes.keepSynced(true);
        referenciaTransacoes.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.clear();
                listaTransacoes.clear();
                double totalDespesaMes = 0;
                double totalRecebidoMes = 0;

                if (dataSnapshot.hasChildren()) {
                    progressBarResumo.setVisibility(View.GONE);
                    textViewNadaARelatarResumo.setText("");
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Transacao transacao = dados.getValue(Transacao.class);
                        keys.add(dados.getKey());
                        listaTransacoes.add(transacao);

                        if (dados.child("tipo").getValue().toString().equals("Gastei")) {
                            totalDespesaMes = totalDespesaMes + Double.valueOf(transacao.getValor());

                        } else {
                            totalRecebidoMes = totalRecebidoMes + Double.valueOf(transacao.getValor());

                        }

                        atualizaDadosResumo(totalRecebidoMes, totalDespesaMes);
                        gerarGrafico(totalDespesaMes);

                    }

                } else {
                    atualizaDadosResumo(totalRecebidoMes, totalDespesaMes);

                    //recuperarResumo();
                    progressBarResumo.setVisibility(View.GONE);
                    textViewNadaARelatarResumo.setText("Você ainda não tem movimentações neste mês!");
                }

                ResumoAdapter adapterTransacoes = new ResumoAdapter(getActivity(), listaTransacoes, keys, ResumoFragment.this);
                adapterTransacoes.notifyDataSetChanged();
                recyclerViewTransacoes.setAdapter(adapterTransacoes);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarApagarCallback(adapterTransacoes));
                itemTouchHelper.attachToRecyclerView(recyclerViewTransacoes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void atualizaDadosResumo(Double saldoPositivo, Double saldoNegativo) {

        double saldoMes = saldoPositivo - saldoNegativo;

        textViewTotalRecebido.setText(FormatarValoresHelper.tratarValores(saldoPositivo));
        textViewTotalGasto.setText(FormatarValoresHelper.tratarValores(saldoNegativo));

        if (saldoMes < 0) {
            textViewValorSaldo.setText(FormatarValoresHelper.tratarValores(saldoMes));
            textViewValorSaldo.setTextColor(ContextCompat.getColor(getContext(), R.color.corBotoesCancela));

        } else {
            textViewValorSaldo.setText(FormatarValoresHelper.tratarValores(saldoMes));
            textViewValorSaldo.setTextColor(ContextCompat.getColor(getContext(), R.color.corBotoesConfirma));

        }

    }

    public void gerarGrafico(double totalGasto) {

        gastoTotal = totalGasto;

        referenciaMetas.child("usuarios").child(idUsuario).child("metas").child(DateCustom.retornaMesAno()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    progressBarGrafico.setVisibility(View.INVISIBLE);
                    pieChartResumo.setVisibility(View.VISIBLE);
                    textViewSemDadosGrafico.setVisibility(View.INVISIBLE);

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Meta metasFirebase = dataSnapshot1.getValue(Meta.class);
                        valorMetas = Double.valueOf(metasFirebase.valorMeta);

                    }

                    double saldoMeta = valorMetas - gastoTotal;
                    ArrayList dadosMetas = new ArrayList();
                    dadosMetas.add(new PieEntry((float) gastoTotal, ""));
                    dadosMetas.add(new PieEntry((float) saldoMeta, ""));

                    PieDataSet dataSet = new PieDataSet(dadosMetas, "");
                    PieData dados = new PieData(dataSet);

                    dados.setValueFormatter(new FormatarValoresHelper());
                    pieChartResumo.setData(dados);

                    dataSet.setColors(new int[]{R.color.corFundoCardViewDespesa, R.color.corFundoCardViewRecebido}, getContext());
                    dataSet.setSliceSpace(2f);
                    dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
                    dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
                    dados.setValueTextSize(12f);
                    dados.setValueTextColor(Color.WHITE);
                    pieChartResumo.setMaxAngle(180f);
                    pieChartResumo.setRotationAngle(180f);
                    pieChartResumo.setRotationEnabled(false);
                    pieChartResumo.getDescription().setEnabled(false);
                    pieChartResumo.getLegend().setEnabled(false);
                    pieChartResumo.setExtraOffsets(5, 5, 5, -150);
                    pieChartResumo.animateX(1000);
                    pieChartResumo.invalidate();

                } else {
                    progressBarGrafico.setVisibility(View.GONE);
                    pieChartResumo.setVisibility(View.GONE);
                    textViewSemDadosGrafico.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        recuperarTransacoes();
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarTransacoes();
    }

    @Override
    public void onResume() {
        super.onResume();
        recuperarTransacoes();
    }

}