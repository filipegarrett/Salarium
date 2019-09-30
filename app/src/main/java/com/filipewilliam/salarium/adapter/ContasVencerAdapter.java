package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.ContasVencer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContasVencerAdapter extends RecyclerView.Adapter<ContasVencerAdapter.ContasVencerViewHolder>{

    Context context;
    ArrayList<ContasVencer> contasVencerArrayList;
    ArrayList<String> keys;
    ContasVencerViewHolder contasVencerViewHolder;

    public ContasVencerAdapter(Context c, ArrayList<ContasVencer> cV, ArrayList<String> k) {

        context = c;
        contasVencerArrayList = cV;
        keys = k;
    }

    @NonNull
    @Override
    public ContasVencerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContasVencerViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_adapter_ultimas_contas_a_vencer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContasVencerViewHolder contasVencerViewHolder, int i) {

        contasVencerViewHolder.categoria.setText(contasVencerArrayList.get(i).getCategoria());
        contasVencerViewHolder.valor.setText(String.valueOf(contasVencerArrayList.get(i).getValor()));
        contasVencerViewHolder.dataVencimento.setText(contasVencerArrayList.get(i).getDataVencimento());
        /*final ContasVencerViewHolder viewHolder = contasVencerViewHolder;
        final int j = i;
        contasVencerViewHolder.buttonExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.excluirDespesa(j);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return contasVencerArrayList.size();
    }

    class ContasVencerViewHolder extends RecyclerView.ViewHolder{

        TextView categoria, valor, dataVencimento;
        ImageButton buttonExcluir;

        public ContasVencerViewHolder(@NonNull View itemView) {
            super(itemView);
            categoria = itemView.findViewById(R.id.textViewTipoDespesa);
            valor = itemView.findViewById(R.id.textViewValorDespesaVencer);
            dataVencimento = itemView.findViewById(R.id.textViewDataVencimentoVencer);
            buttonExcluir = itemView.findViewById(R.id.imageButtonExcluirDespesa);

        }

        public void excluirDespesa(int i){

            String key = keys.get(i);
            DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("contas-a-vencer");
            referencia.child(key).removeValue();

        }

    }
}