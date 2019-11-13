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
    private String mesAtual = DateCustom.retornaMesAno();
    private ArrayList<String> keys = new ArrayList<>();
    private static MainActivity activityInstancia;

    public ResumoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activityInstancia = (MainActivity) getContext();
        recuperarTransacoes();
        recuperarResumo();
        //new AtualizarResumoAsyncTask(this).execute();
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);
        textViewValorSaldo = view.findViewById(R.id.textViewValorSaldo);
        textViewTotalRecebido = view.findViewById(R.id.textViewTotalRecebido);
        textViewTotalGasto = view.findViewById(R.id.textViewTotalGasto);
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
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Transacao transacao = dados.getValue(Transacao.class);
                    keys.add(dados.getKey());
                    listaTransacoes.add(transacao);
                }

                ResumoAdapter adapterTransacoes = new ResumoAdapter(getActivity(), listaTransacoes, keys);
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

    /*private static class AtualizarResumoAsyncTask extends AsyncTask<Void, Void, double[]> {

        final DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        private String mesAtual = DateCustom.retornaMesAno();
        Resultados r = new Resultados();
        double totalDespesaMes = 0;
        double totalRecebidoMes = 0;

        public class Resultados{

            public double totalRecebido, totalDespesas = 0.0;

            public Resultados() {
            }

            public double getTotalRecebidoMes() {
                return totalRecebido;
            }

            public void setTotalRecebidoMes(double totalRecebidoMes) {
                this.totalRecebido = totalRecebidoMes;
            }

            public double getTotalDespesasMes() {
                return totalDespesas;
            }

            public void setTotalDespesasMes(double totalDespesasMes) {
                this.totalDespesas = totalDespesasMes;
            }
        }

        private WeakReference<ResumoFragment> fragmentWeakReference;

        // a ideia aqui dessa WeakReference e construtor customizado é impedir problemas de vazamento de memória
        AtualizarResumoAsyncTask(ResumoFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }


        @Override
        protected double[] doInBackground(Void... params) {

            final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
            referencia.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Transacao transacao = dataSnapshot1.getValue(Transacao.class);

                        if (dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")) {
                            totalDespesaMes = totalDespesaMes + transacao.getValor();
                            System.out.println(totalDespesaMes);
                            r.setTotalDespesasMes(totalDespesaMes);

                        } else {
                            totalRecebidoMes = totalRecebidoMes + Double.valueOf(transacao.getValor());

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            System.out.println("rerwerwrwe " + totalRecebidoMes);
            return new double[] {totalRecebidoMes, totalDespesaMes};
        }

        @Override
        protected void onPostExecute(double... doubles) {
            ResumoFragment fragment = fragmentWeakReference.get();
            if (fragment == null || fragment.isRemoving()) return;

            System.out.println("dsdadad" + doubles[0]);

            TextView textViewSaldo = fragment.getView().findViewById(R.id.textViewValorSaldo);

            double saldoMes = r.getTotalRecebidoMes() - r.getTotalDespesasMes();
            fragment.textViewTotalRecebido.setText(FormatarValoresHelper.tratarValores(doubles[0]));
            fragment.textViewTotalGasto.setText(FormatarValoresHelper.tratarValores(doubles[1]));

            if (saldoMes < 0) {
                textViewSaldo.setText(FormatarValoresHelper.tratarValores(saldoMes));
                textViewSaldo.setTextColor(ContextCompat.getColor(activityInstancia.getApplicationContext(), R.color.corBotoesCancela));

            } else {
                fragment.textViewValorSaldo.setText(FormatarValoresHelper.tratarValores(saldoMes));
                fragment.textViewValorSaldo.setTextColor(ContextCompat.getColor(activityInstancia.getApplicationContext(), R.color.corBotoesConfirma));

            }
        }
    }*/

}