package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.model.ContasVencer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class ContasVencerAdapter extends RecyclerView.Adapter<ContasVencerAdapter.ContasVencerViewHolder>{

    private Context context;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private FormatarValoresHelper tratarValores;
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
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
    public void onBindViewHolder(@NonNull final ContasVencerViewHolder contasVencerViewHolder, final int i) {
        contasVencerViewHolder.categoria.setText(contasVencerArrayList.get(i).getCategoria());
        contasVencerViewHolder.valor.setText(tratarValores.tratarValores(contasVencerArrayList.get(i).getValor()));
        contasVencerViewHolder.dataVencimento.setText("Vence em: " + contasVencerArrayList.get(i).getDataVencimento());

    }

    public void excluirItem(int posicao){
        String key = keys.get(posicao);
        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child(idUsuario).child("contas-a-vencer");
        contasVencerArrayList.remove(posicao);
        referencia.child(key).removeValue(mRemoveListener);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, contasVencerArrayList.size());
        notifyDataSetChanged();

    }

    public Context gerarContext(){
        return this.context;
    }

    @Override
    public int getItemCount() {
        return contasVencerArrayList.size();
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

    class ContasVencerViewHolder extends RecyclerView.ViewHolder{

        TextView categoria, valor, dataVencimento;

        public ContasVencerViewHolder(@NonNull View itemView) {
            super(itemView);
            categoria = itemView.findViewById(R.id.textViewTipoDespesa);
            valor = itemView.findViewById(R.id.textViewValorDespesaVencer);
            dataVencimento = itemView.findViewById(R.id.textViewDataVencimentoVencer);

        }

    }
}
