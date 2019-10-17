package com.filipewilliam.salarium.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.model.Meta;
import java.util.List;

public class MetasAdapter extends RecyclerView.Adapter<MetasAdapter.MyViewHolder> {

    private List<Meta> listaMetas;

    public MetasAdapter(List<Meta> lista) {
        this.listaMetas = lista;
    }

    @NonNull
    @Override
    public MetasAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemListaMetas = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_adapter_metas, viewGroup, false);
        return new MyViewHolder(itemListaMetas);
    }

    @Override
    public void onBindViewHolder(@NonNull MetasAdapter.MyViewHolder myViewHolder, int i) {

        Meta meta = listaMetas.get(i);
        myViewHolder.valorMeta.setText(String.valueOf(meta.getValor()));
        myViewHolder.dataMeta.setText(meta.getData());

    }

    @Override
    public int getItemCount () {
        return listaMetas.size();
    }

    //Classe viewholder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView valorMeta;
        TextView dataMeta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            valorMeta = itemView.findViewById(R.id.textViewValorMeta);
            dataMeta = itemView.findViewById(R.id.textViewDataMeta);
        }
    }
}