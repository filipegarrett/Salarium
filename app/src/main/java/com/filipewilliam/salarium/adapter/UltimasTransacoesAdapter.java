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

public class UltimasTransacoesAdapter extends RecyclerView.Adapter<UltimasTransacoesAdapter.MyViewHolder> {

    private List<Transacao> listaTransacoes;

    public UltimasTransacoesAdapter(List<Transacao> lista) {
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
        myViewHolder.tipoTransacao.setText(transacao.getTipo());
        myViewHolder.valorTransacao.setText(String.valueOf(transacao.getValor()));
        myViewHolder.categoriaTransacao.setText(transacao.getCategoria());
        myViewHolder.dataTransacao.setText(transacao.getData());
        myViewHolder.setaTransacao.setImageResource(R.drawable.ic_arrow_upward_verde_24dp);
        if (myViewHolder.tipoTransacao.equals("Gasto")){
            myViewHolder.setaTransacao.setImageResource(R.drawable.ic_arrow_downward_vermelho_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return listaTransacoes.size();
    }

    //Classe viewholder
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tipoTransacao;
        TextView categoriaTransacao;
        TextView valorTransacao;
        TextView dataTransacao;
        ImageView setaTransacao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tipoTransacao = itemView.findViewById(R.id.textViewEmailUsuario);
            categoriaTransacao = itemView.findViewById(R.id.textViewCategoriaTransacao);
            valorTransacao = itemView.findViewById(R.id.textViewValorTransacao);
            dataTransacao = itemView.findViewById(R.id.textViewDataTransacao);
            setaTransacao = itemView.findViewById(R.id.imageViewSeta);
        }
    }

}