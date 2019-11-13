package com.filipewilliam.salarium.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.fragments.ResumoFragment;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ResumoAdapter extends RecyclerView.Adapter<ResumoAdapter.ResumoViewHolder> {

    private Context context;
    private DateCustom dateCustom;
    private String mesAtual = dateCustom.retornaMesAno();
    private FormatarValoresHelper tratarValores;
    ArrayList<Transacao> transacoesArrayList;
    ArrayList<String> keys;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
    private ArrayList<Double> valores = new ArrayList<>();
    private ArrayList<Transacao> listaTransacoes = new ArrayList<>();
    private TextView textViewTotalGasto;
    private TextView textViewTotalRecebido;
    private TextView textViewValorSaldo;

    public ResumoAdapter(Context c, ArrayList<Transacao> tArrayList, ArrayList<String> k) {
        this.context = c;
        keys = k;
        this.transacoesArrayList = tArrayList;
    }

    @NonNull
    @Override
    public ResumoAdapter.ResumoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemListaTransacoes = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_relatorios, viewGroup, false);
        return new ResumoViewHolder(itemListaTransacoes);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumoViewHolder resumoViewHolder, int i) {
        Transacao transacao = transacoesArrayList.get(i);

        if(transacao.getTipo().contains("Gastei")){
            resumoViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewDespesa));
            resumoViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            resumoViewHolder.categoria.setText(transacao.getCategoria() + ":");
            resumoViewHolder.descricao.setText(transacao.getDescricao());
            resumoViewHolder.valor.setText(tratarValores.tratarValores(transacao.getValor()));
            resumoViewHolder.data.setText(transacao.getData());

        }else{
            resumoViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewRecebido));
            resumoViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            resumoViewHolder.categoria.setText(transacao.getCategoria() + ":");
            resumoViewHolder.descricao.setText(transacao.getDescricao());
            resumoViewHolder.valor.setText(tratarValores.tratarValores(transacao.getValor()));
            resumoViewHolder.data.setText(transacao.getData());
        }

    }

    //remove a transação selecionada do recylcerview e do firebase
    public void excluirItem(int posicao){
        final int posicaoSelecionada = posicao;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Excluir transação");
        alertDialog.setMessage("Deseja realmente excluir este lançamento?");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String key = keys.get(posicaoSelecionada);
                DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child(idUsuario).child("transacao").child(mesAtual);
                transacoesArrayList.remove(posicaoSelecionada);
                referencia.child(key).removeValue(mRemoveListener);
                notifyItemRemoved(posicaoSelecionada);
                //recuperarResumo();
                iniciarAsyncTask();
                notifyItemRangeChanged(posicaoSelecionada, transacoesArrayList.size());
                notifyDataSetChanged();

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show();
                //recuperarResumo();
                notifyDataSetChanged();

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    public void iniciarAsyncTask(){

        //AsyncAtualizarResumo task = new AsyncAtualizarResumo();
        //task.execute();
        ResumoFragment resumoFragment = new ResumoFragment();
        resumoFragment.recuperarResumo();

    }

    @Override
    public int getItemCount() {
        return transacoesArrayList.size();
    }

    public Context gerarContext(){
        return this.context;
    }

    private DatabaseReference.CompletionListener mRemoveListener = //útil para acompanhar e verificar se determinada entrada foi efetivamente removida do banco
            new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    if (error == null) {
                        Log.d(TAG, "Removed: " + ref);
                        // or you can use:
                        System.out.println("Removed: " + ref);
                    } else {
                        Log.e(TAG, "Remove of " + ref + " failed: " + error.getMessage());
                    }
                }
            };

    class ResumoViewHolder extends RecyclerView.ViewHolder{

        TextView tipo, categoria, descricao, valor, data;

        public ResumoViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.textViewTipoRelatorio);
            categoria = itemView.findViewById(R.id.textViewCategoriaRelatorio);
            descricao = itemView.findViewById(R.id.textViewDescricaoRelatorio);
            valor = itemView.findViewById(R.id.textViewValorRelatorio);
            data = itemView.findViewById(R.id.textViewDataRelatorio);
            textViewValorSaldo = itemView.findViewById(R.id.textViewValorSaldo);
            textViewTotalRecebido = itemView.findViewById(R.id.textViewTotalRecebido);
            textViewTotalGasto = itemView.findViewById(R.id.textViewTotalGasto);

        }

    }

    class AsyncAtualizarResumo extends AsyncTask<Void, Void, ArrayList<Double>> {


        @Override
        protected ArrayList<Double> doInBackground(Void... params) {
            final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
            referencia.child("usuarios").child(idUsuario).child("transacao").child(mesAtual).orderByChild("data").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaTransacoes.clear();
                    valores.clear();
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
                    }
                    valores.add(totalDespesaMes);
                    valores.add(totalRecebidoMes);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            return valores;
        }

        @Override
        protected void onPostExecute(ArrayList<Double> doubles) {
            super.onPostExecute(doubles);

            Double saldoMes = (doubles.get(1) - (doubles.get(0)));
            textViewTotalRecebido.setText(tratarValores.tratarValores(doubles.get(1)));
            textViewTotalGasto.setText(tratarValores.tratarValores(doubles.get(0)));

            if (saldoMes < 0) {
                textViewValorSaldo.setText(tratarValores.tratarValores(saldoMes));
                textViewValorSaldo.setTextColor(ContextCompat.getColor(gerarContext(), R.color.corBotoesCancela));

            } else {
                textViewValorSaldo.setText(tratarValores.tratarValores(saldoMes));
                textViewValorSaldo.setTextColor(ContextCompat.getColor(gerarContext(), R.color.corBotoesConfirma));

            }
        }
    }
}
