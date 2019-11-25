package com.filipewilliam.salarium.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Categoria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CategoriasViewHolder> {

    private Context context;
    private String tipo;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());
    ArrayList<Categoria> categoriasArrayList;
    ArrayList<String> keys;

    public CategoriasAdapter(Context c, ArrayList<Categoria> cat, ArrayList<String> k, String t) {
        context = c;
        categoriasArrayList = cat;
        keys = k;
        tipo = t;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == categoriasArrayList.size()) ? R.layout.layout_adapter_categorias_adicionar : R.layout.layout_adapter_categorias;
    }

    @NonNull
    @Override
    public CategoriasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //return new CategoriasViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_adapter_categorias, viewGroup, false));

        View itemView;

        if (viewType == R.layout.layout_adapter_categorias_adicionar) {
            itemView = LayoutInflater.from(context).inflate(R.layout.layout_adapter_categorias_adicionar, viewGroup, false);
            CategoriasViewHolder categoriasViewHolder = new CategoriasViewHolder(itemView);
            categoriasViewHolder.itemView.setTag("bloqueado");
            return categoriasViewHolder;
        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.layout_adapter_categorias, viewGroup, false);
            CategoriasViewHolder categoriasViewHolder = new CategoriasViewHolder(itemView);
            categoriasViewHolder.itemView.setTag("normal");
            return categoriasViewHolder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull CategoriasViewHolder categoriasViewHolder, int i) {

        if (i == categoriasArrayList.size()) {
            categoriasViewHolder.novaCategoria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("Criar nova categoria");
                    dialog.setCancelable(true);
                    //necessário estes parâmetros pois somente o edittext não aparecia.
                    final EditText categoria = new EditText(context);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    categoria.setLayoutParams(lp);
                    dialog.setView(categoria);


                    dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    dialog.setPositiveButton("Criar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Categoria novaCategoria = new Categoria();
                            novaCategoria.setDescricaoCategoria(categoria.getText().toString());
                            novaCategoria.salvarCategoria(tipo);
                            Toast.makeText(context, "Categoria criada com sucesso!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dialog.create();
                    dialog.show();
                }
            });
        } else {
            categoriasViewHolder.categoria.setText(categoriasArrayList.get(i).getDescricaoCategoria());
        }
        //categoriasViewHolder.categoria.setText(categoriasArrayList.get(i).getDescricaoCategoria());

    }

    public Context gerarContext() {
        return this.context;
    }

    public void excluirItem(int posicao) {
        String key = keys.get(posicao);
        DatabaseReference referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child(idUsuario).child(tipo);
        categoriasArrayList.remove(posicao);
        referencia.child(key).removeValue();
        notifyItemRemoved(posicao);
        notifyItemRangeChanged(posicao, categoriasArrayList.size());
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return categoriasArrayList.size() + 1;
    }

    class CategoriasViewHolder extends RecyclerView.ViewHolder {

        TextView categoria;
        TextView novaCategoria;

        public CategoriasViewHolder(@NonNull View itemView) {
            super(itemView);
            categoria = itemView.findViewById(R.id.textViewNomeCategoria);
            novaCategoria = itemView.findViewById(R.id.textViewNovaCategoria);

        }
    }
}
