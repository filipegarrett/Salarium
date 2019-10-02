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
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.ContasVencer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ContasVencerAdapter extends RecyclerView.Adapter<ContasVencerAdapter.ContasVencerViewHolder>{

    private Context context;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
    ArrayList<ContasVencer> contasVencerArrayList;
    ArrayList<String> keys;
    String keyMes;
    ContasVencerViewHolder contasVencerViewHolder;

    public ContasVencerAdapter(Context c, ArrayList<ContasVencer> cV, ArrayList<String> k, String kM) {

        context = c;
        contasVencerArrayList = cV;
        keys = k;
        keyMes = kM;
    }

    @NonNull
    @Override
    public ContasVencerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ContasVencerViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_adapter_ultimas_contas_a_vencer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContasVencerViewHolder contasVencerViewHolder, int i) {

        contasVencerViewHolder.categoria.setText(contasVencerArrayList.get(i).getCategoria());
        contasVencerViewHolder.valor.setText("R$ " + String.valueOf(contasVencerArrayList.get(i).getValor()) + "0");
        contasVencerViewHolder.dataVencimento.setText("Vence em: " + contasVencerArrayList.get(i).getDataVencimento());

    }

    public void excluirItem(int posicao){
        String key = keys.get(posicao);
        System.out.println(keys);
        System.out.println(key);
        System.out.println(keyMes);
        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child(idUsuario).child("contas-a-vencer").child(keyMes);
        referencia.child(key).setValue(null);
        contasVencerArrayList.remove(posicao);
        notifyDataSetChanged();
        notifyItemRemoved(posicao);

    }

    public Context gerarContext(){
        return this.context;
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
            //buttonExcluir = itemView.findViewById(R.id.imageButtonExcluirDespesa);

        }

    }
}
