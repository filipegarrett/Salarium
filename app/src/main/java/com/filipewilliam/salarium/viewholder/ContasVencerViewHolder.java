package com.filipewilliam.salarium.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.filipewilliam.salarium.R;

public class ContasVencerViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewTipoDespesa;
    public TextView textViewValorDespesa;
    public TextView textViewCategoriaDespesa;
    public TextView textViewDataVencimento;
    public ImageView buttonExcluirDespesa;

    public ContasVencerViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewTipoDespesa = itemView.findViewById(R.id.textViewTipoDespesa);
        textViewValorDespesa = itemView.findViewById(R.id.textViewValorDespesaVencer);
        textViewCategoriaDespesa = itemView.findViewById(R.id.textViewCategoriaDespesaVencer);
        textViewDataVencimento = itemView.findViewById(R.id.textViewDataVencimentoVencer);
        buttonExcluirDespesa = itemView.findViewById(R.id.imageViewExcluirContasVencer);

    }


}
