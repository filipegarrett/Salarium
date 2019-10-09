package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.Transacao;

import java.util.ArrayList;

public class RelatoriosAdapter extends RecyclerView.Adapter<RelatoriosAdapter.RelatoriosViewHolder> {

    private Context context;
    private FormatarValoresHelper tratarValores;
    ArrayList<Transacao> transacoesArrayList;

    public RelatoriosAdapter(Context c, ArrayList<Transacao> tArrayList) {
        this.context = c;
        this.transacoesArrayList = tArrayList;
    }

    @NonNull
    @Override
    public RelatoriosAdapter.RelatoriosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemListaRelatorio = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_relatorios, viewGroup, false);
        return new RelatoriosViewHolder(itemListaRelatorio);
    }

    @Override
    public void onBindViewHolder(@NonNull RelatoriosAdapter.RelatoriosViewHolder relatoriosViewHolder, int i) {
        Transacao transacao = transacoesArrayList.get(i);

        if(transacao.getTipo().contains("Gastei")){
            relatoriosViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewDespesa));
            relatoriosViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            relatoriosViewHolder.categoria.setText(transacao.getCategoria() + ":");
            relatoriosViewHolder.descricao.setText(transacao.getDescricao());
            relatoriosViewHolder.valor.setText(tratarValores.tratarValores(transacao.getValor()));
            relatoriosViewHolder.data.setText(transacao.getData());

        }else{
            relatoriosViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.corFundoCardViewRecebido));
            relatoriosViewHolder.tipo.setText(transacoesArrayList.get(i).getTipo());
            relatoriosViewHolder.categoria.setText(transacao.getCategoria() + ":");
            relatoriosViewHolder.descricao.setText(transacao.getDescricao());
            relatoriosViewHolder.valor.setText(tratarValores.tratarValores(transacao.getValor()));
            relatoriosViewHolder.data.setText(transacao.getData());
        }

    }

    @Override
    public int getItemCount() {
        return transacoesArrayList.size();
    }

    class RelatoriosViewHolder extends RecyclerView.ViewHolder{

        TextView tipo, categoria, descricao, valor, data;

        public RelatoriosViewHolder(@NonNull View itemView) {
            super(itemView);
            tipo = itemView.findViewById(R.id.textViewTipoRelatorio);
            categoria = itemView.findViewById(R.id.textViewCategoriaRelatorio);
            descricao = itemView.findViewById(R.id.textViewDescricaoRelatorio);
            valor = itemView.findViewById(R.id.textViewValorRelatorio);
            data = itemView.findViewById(R.id.textViewDataRelatorio);

        }

    }
}
