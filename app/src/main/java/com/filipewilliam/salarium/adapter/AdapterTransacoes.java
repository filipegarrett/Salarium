package com.filipewilliam.salarium.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.Transacao;

import java.util.List;

public class AdapterTransacoes extends RecyclerView.Adapter<AdapterTransacoes.MyViewHolder> {

    private List<Transacao> listaTransacoes;

    public AdapterTransacoes(List<Transacao> lista) {
        this.listaTransacoes = lista;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemListaTransacoes = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_ultimas_transacoes, viewGroup, false);

        return new MyViewHolder(itemListaTransacoes);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Transacao transacao = listaTransacoes.get(i);
        myViewHolder.tipoTransacao.setText(transacao.getTipoTransacao());
        myViewHolder.valorTransacao.setText(transacao.getValorTransacao());
        myViewHolder.categoriaTransacao.setText(transacao.getCategoriaTransacao());
        myViewHolder.dataTransacao.setText(transacao.getDataTransacao());

        if (myViewHolder.tipoTransacao.equals("Gastei")){
            myViewHolder.setaTransacao.setImageResource(R.drawable.ic_arrow_downward_vermelho_24dp);
        } else {
            myViewHolder.setaTransacao.setImageResource(R.drawable.ic_arrow_upward_verde_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return listaTransacoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tipoTransacao;
        TextView categoriaTransacao;
        TextView valorTransacao;
        TextView dataTransacao;
        ImageView setaTransacao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tipoTransacao = itemView.findViewById(R.id.textViewTipoTransacao);
            categoriaTransacao = itemView.findViewById(R.id.textViewCategoriaGasto);
            valorTransacao = itemView.findViewById(R.id.textViewValorGasto);
            dataTransacao = itemView.findViewById(R.id.textViewDataTransacao);
            setaTransacao = itemView.findViewById(R.id.imageViewSeta);
        }
    }

}