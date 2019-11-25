package com.filipewilliam.salarium.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ResumoAdapter extends RecyclerView.Adapter<ResumoAdapter.ResumoViewHolder> {

    private Context context;
    private String mesAtual = DateCustom.retornaMesAno();
    private ResumoFragment resumoFragment;
    ArrayList<Transacao> transacoesArrayList;
    ArrayList<String> keys;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

    public ResumoAdapter(Context c, ArrayList<Transacao> tArrayList, ArrayList<String> k, ResumoFragment fragment) {
        this.context = c;
        keys = k;
        this.transacoesArrayList = tArrayList;
        this.resumoFragment = fragment;
    }

    @NonNull
    @Override
    public ResumoAdapter.ResumoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //View itemListaTransacoes = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_relatorios, viewGroup, false);
        View itemView;
        itemView = LayoutInflater.from(context).inflate(R.layout.layout_adapter_relatorios, viewGroup, false);
        ResumoViewHolder resumoViewHolder = new ResumoViewHolder(itemView);
        resumoViewHolder.itemView.setTag("normal");

        return resumoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ResumoViewHolder resumoViewHolder, int i) {
        Transacao transacao = transacoesArrayList.get(i);

        if(transacao.getTipo().contains("Gastei")){
            resumoViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewDespesa));
            resumoViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            resumoViewHolder.categoria.setText(transacao.getCategoria() + ":");
            resumoViewHolder.descricao.setText(transacao.getDescricao());
            resumoViewHolder.valor.setText(FormatarValoresHelper.tratarValores(transacao.getValor()));
            resumoViewHolder.data.setText(transacao.getData());

        }else{
            resumoViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewRecebido));
            resumoViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            resumoViewHolder.categoria.setText(transacao.getCategoria() + ":");
            resumoViewHolder.descricao.setText(transacao.getDescricao());
            resumoViewHolder.valor.setText(FormatarValoresHelper.tratarValores(transacao.getValor()));
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
                notifyItemRangeChanged(posicaoSelecionada, transacoesArrayList.size());
                notifyDataSetChanged();
                resumoFragment.recuperarResumo();

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Cancelado", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
                resumoFragment.recuperarResumo();

            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();

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

        }

    }

}
