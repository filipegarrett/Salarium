package com.filipewilliam.salarium.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.AdapterTransacoes;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.model.Transacao;
import com.filipewilliam.salarium.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.filipewilliam.salarium.helpers.DateCustom.retornaMesAno;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {

    private List<Transacao> listaTransacoes = new ArrayList<>();
    private RecyclerView recyclerViewTransacoes;
    private DateCustom dateCustom;
    private AdapterTransacoes adapterTransacoes;
    public FragmentPagerItemAdapter adapterView;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private TextView textViewTotalGasto;
    private TextView textViewTotalRecebido;
    private TextView textViewValorSaldo;
    private Double gastoTotal;
    private Double recebimentoTotal;
    private Double saldoTotal;
    private String mesAtual = dateCustom.retornaMesAno();

    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        recuperarTransacoes();
        recuperarTotais();
        //inflando layout de resumo
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);
        textViewValorSaldo = view.findViewById(R.id.textViewValorSaldo);
        textViewTotalRecebido = view.findViewById(R.id.textViewTotalRecebido);
        textViewTotalGasto = view.findViewById(R.id.textViewTotalGasto);
        recyclerViewTransacoes = view.findViewById(R.id.recyclerViewTransacoes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTransacoes.setLayoutManager(layoutManager);
        recyclerViewTransacoes.setHasFixedSize(true);
        recyclerViewTransacoes.addItemDecoration(new DividerItemDecoration(this.getContext(), ((LinearLayoutManager) layoutManager).getOrientation()));

        return view;

    }

    //recupera as transações do mês atual e preenche na recycler view
    public void recuperarTransacoes(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference referenciaTransacoes = FirebaseDatabase.getInstance().getReference();
        referenciaTransacoes.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaTransacoes.clear();
                    for (DataSnapshot dados: dataSnapshot.getChildren()){
                        Transacao transacao = dados.getValue(Transacao.class);
                        listaTransacoes.add(transacao);

                    }

                    AdapterTransacoes adapterTransacoes = new AdapterTransacoes(listaTransacoes);
                    recyclerViewTransacoes.setAdapter(adapterTransacoes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //recupera os totais de gastos e recebimentos e mostra saldo atual
    public void recuperarTotais (){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64(emailUsuario);
        DatabaseReference usuarioRef = referencia.child("usuarios").child( idUsuario );

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                gastoTotal = usuario.getGastoTotal();
                recebimentoTotal = usuario.getRecebimentoTotal();
                saldoTotal = recebimentoTotal - gastoTotal;
                textViewTotalGasto.setText(gastoTotal.toString());
                textViewTotalRecebido.setText(recebimentoTotal.toString());
                textViewValorSaldo.setText(saldoTotal.toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}