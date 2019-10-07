package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.helpers.InvestimentosHelper;
import com.filipewilliam.salarium.model.Transacao;

import java.util.ArrayList;

public class RelatoriosAdapter extends RecyclerView.Adapter<RelatoriosAdapter.RelatoriosViewHolder> {

    private Context context;
    private InvestimentosHelper tratarValores;
    ArrayList<Transacao> transacoesArrayList;

    public RelatoriosAdapter(ArrayList<Transacao> tArrayList) {
        this.transacoesArrayList = tArrayList;
    }

    @NonNull
    @Override
    public RelatoriosAdapter.RelatoriosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RelatoriosViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_ultimas_contas_a_vencer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RelatoriosAdapter.RelatoriosViewHolder relatoriosViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class RelatoriosViewHolder extends RecyclerView.ViewHolder{

        TextView categoria, valor, dataVencimento;

        public RelatoriosViewHolder(@NonNull View itemView) {
            super(itemView);
            categoria = itemView.findViewById(R.id.textViewTipoDespesa);
            valor = itemView.findViewById(R.id.textViewValorDespesaVencer);
            dataVencimento = itemView.findViewById(R.id.textViewDataVencimentoVencer);

        }

    }
}
