package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
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

public class UltimasTransacoesAdapter extends RecyclerView.Adapter<UltimasTransacoesAdapter.MyViewHolder> {

    private Context context;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
    private ArrayList<Transacao> listaTransacoes;
    ArrayList<String> keys;
    private DateCustom dateCustom;
    private String mesAtual = dateCustom.retornaMesAno();
    private MyViewHolder myViewHolder;

    public UltimasTransacoesAdapter(Context c, ArrayList<Transacao> lista, ArrayList<String> k) {
        context = c;
        keys = k;
        listaTransacoes = lista;
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

    public void excluirItem(int posicao){
        String key = keys.get(posicao);
        System.out.println(key);
        System.out.println(mesAtual);
        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child(idUsuario).child("transacao").child(mesAtual);
        listaTransacoes.remove(posicao);
        referencia.child(key).removeValue(mRemoveListener);
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, listaTransacoes.size());
        notifyDataSetChanged();

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