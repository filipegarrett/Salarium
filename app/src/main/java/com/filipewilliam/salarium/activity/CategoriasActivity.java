package com.filipewilliam.salarium.activity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.CategoriasAdapter;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.DeslizarApagarCallback;
import com.filipewilliam.salarium.model.Categoria;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CategoriasActivity extends AppCompatActivity {

    private String tipo;
    private RecyclerView recyclerViewCategorias;
    private Button buttonSalvarCategorias;
    private CategoriasAdapter adapter;
    private ArrayList<Categoria> listaCategorias;
    private ArrayList<String> keys = new ArrayList<>();
    private final Date hoje = DateCustom.retornaDataHojeDateFormat();
    private final SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
    private ValueEventListener valueEventListenerRecycler;
    private DatabaseReference referencia2 = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tipo = extras.getString("TIPO");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int largura = displayMetrics.widthPixels;
        int altura = displayMetrics.heightPixels;

        getWindow().setLayout((int) (largura*.7), (int) (altura*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        recyclerViewCategorias = findViewById(R.id.recyclerViewCategorias);
        recuperaCategorias(tipo);
        buttonSalvarCategorias = findViewById(R.id.buttonCriarCategoria);

        listaCategorias = new ArrayList<Categoria>();

        buttonSalvarCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void recuperaCategorias(String tipo){

        if(tipo.equals("gasto")){

            valueEventListenerRecycler = referencia2.child("usuarios").child(idUsuario).child("categorias_gastos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    keys.clear();
                    listaCategorias.clear();

                    if(dataSnapshot.hasChildren()){

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Categoria categoria = dataSnapshot1.getValue(Categoria.class);
                            keys.add(dataSnapshot1.getKey());
                            listaCategorias.add(categoria);
                        }
                    } else {
                    /*progressBar.setVisibility(View.GONE);
                    textViewSemContaCadastrada.setText("Você não tem nenhuma despesa para pagar =)");*/
                    }

                    adapter = new CategoriasAdapter(CategoriasActivity.this, listaCategorias, keys, "categorias_gastos");
                    adapter.notifyDataSetChanged();
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewCategorias.setLayoutManager(layoutManager);
                    recyclerViewCategorias.setHasFixedSize(true);
                    recyclerViewCategorias.setAdapter(adapter);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarApagarCallback(adapter));
                    itemTouchHelper.attachToRecyclerView(recyclerViewCategorias);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CategoriasActivity.this, "Opsss, algo deu errado =/", Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            valueEventListenerRecycler = referencia2.child("usuarios").child(idUsuario).child("categorias_recebimentos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    keys.clear();
                    listaCategorias.clear();

                    if(dataSnapshot.hasChildren()){

                        for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                            Categoria categoria = dataSnapshot1.getValue(Categoria.class);
                            keys.add(dataSnapshot1.getKey());
                            listaCategorias.add(categoria);
                        }
                    } else {
                    /*progressBar.setVisibility(View.GONE);
                    textViewSemContaCadastrada.setText("Você não tem nenhuma despesa para pagar =)");*/
                    }

                    adapter = new CategoriasAdapter(CategoriasActivity.this, listaCategorias, keys, "categorias_recebimentos");
                    adapter.notifyDataSetChanged();
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewCategorias.setLayoutManager(layoutManager);
                    recyclerViewCategorias.setHasFixedSize(true);
                    recyclerViewCategorias.setAdapter(adapter);
                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DeslizarApagarCallback(adapter));
                    itemTouchHelper.attachToRecyclerView(recyclerViewCategorias);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CategoriasActivity.this, "Opsss, algo deu errado =/", Toast.LENGTH_SHORT).show();

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}
