package com.filipewilliam.salarium.fragments;


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
import com.filipewilliam.salarium.activity.MainActivity;
import com.filipewilliam.salarium.adapter.ResumoAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.DeslizarApagarCallback;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {

    private ArrayList<Transacao> listaTransacoes = new ArrayList<>();
    private RecyclerView recyclerViewTransacoes;
    public FragmentPagerItemAdapter adapterView;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private TextView textViewTotalGasto;
    private TextView textViewTotalRecebido;
    private TextView textViewValorSaldo;
    private ProgressBar progressBarResumo;
    private TextView textViewNadaARelatarResumo;
    private String mesAtual = DateCustom.retornaMesAno();
    private ArrayList<String> keys = new ArrayList<>();

    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recuperarTransacoes();
        recuperarResumo();
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);
        textViewValorSaldo = view.findViewById(R.id.textViewValorSaldo);
        textViewTotalRecebido = view.findViewById(R.id.textViewTotalRecebido);
        textViewTotalGasto = view.findViewById(R.id.textViewTotalGasto);
        textViewNadaARelatarResumo = view.findViewById(R.id.textViewNadaARelatarResumo);
        progressBarResumo = view.findViewById(R.id.progressBarResumo);
        progressBarResumo.setVisibility(View.VISIBLE);
        recyclerViewTransacoes = view.findViewById(R.id.recyclerViewTransacoes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTransacoes.setLayoutManager(layoutManager);
        recyclerViewTransacoes.setHasFixedSize(true);

        return view;

    }

    //recupera as transações do mês atual e preenche na recycler view
    public void recuperarTransacoes() {

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference referenciaTransacoes = FirebaseDatabase.getInstance().getReference();
        referenciaTransacoes.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                keys.clear();
                listaTransacoes.clear();

                if(dataSnapshot.hasChildren()){
                    progressBarResumo.setVisibility(View.GONE);
                    textViewNadaARelatarResumo.setText("");
                    for (DataSnapshot dados : dataSnapshot.getChildren()) {
                        Transacao transacao = dados.getValue(Transacao.class);
                        keys.add(dados.getKey());
                        listaTransacoes.add(transacao);
                    }

                }else{
                    recuperarResumo();
                    progressBarResumo.setVisibility(View.GONE);
                    textViewNadaARelatarResumo.setText("Você ainda não tem movimentações cadastradas neste mês!");
                }


                ResumoAdapter adapterTransacoes = new ResumoAdapter(getActivity(), listaTransacoes, keys, ResumoFragment.this);
                recyclerViewTransacoes.setAdapter(adapterTransacoes);
                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarApagarCallback(adapterTransacoes));
                itemTouchHelper.attachToRecyclerView(recyclerViewTransacoes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //recupera os totais de gastos e recebimentos e mostra saldo atual
    public void recuperarResumo() {

        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
        referencia.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTransacoes.clear();
                double totalDespesaMes = 0;
                double totalRecebidoMes = 0;
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Transacao transacao = dataSnapshot1.getValue(Transacao.class);
                    listaTransacoes.add(transacao);

                    if (dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")) {
                        totalDespesaMes = totalDespesaMes + Double.valueOf(transacao.getValor());

                    } else {
                        totalRecebidoMes = totalRecebidoMes + Double.valueOf(transacao.getValor());

                    }

                    atualizaDadosResumo(transacao, totalRecebidoMes, totalDespesaMes);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizaDadosResumo(Transacao transacao, Double saldoPositivo, Double saldoNegativo) {

        Double saldoMes = saldoPositivo - saldoNegativo;

        System.out.println(transacao.getValor());
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

    @Override
    public void onStart() {
        super.onStart();
        recuperarResumo();
    }

    @Override
    public void onResume() {
        super.onResume();
        recuperarResumo();
    }
}