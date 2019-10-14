package com.filipewilliam.salarium.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.model.Transacao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetasActivity extends AppCompatActivity {

    private Spinner spinnerMetas;
    private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DateCustom dateCustom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Metas");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas);
        final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

        spinnerMetas = findViewById(R.id.spinnerMesMetas);

        referencia.child("usuarios").child(idUsuario).child("meta").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getChildrenCount() > 0){
                    List<String> listMetasMeses = new ArrayList<String>();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        listMetasMeses.add(dateCustom.formatarMesAno(dataSnapshot1.getKey()));
                        Collections.reverse(listMetasMeses); //Não é muito elegante, mas o Firebase não conhece o conceito de ordenar dados de forma decrescente...

                        ArrayAdapter<String> metasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listMetasMeses);
                        metasAdapter.setDropDownViewResource(android.R.layout.simple_selectable_list_item);
                        spinnerMetas.setAdapter(metasAdapter);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    //método para voltar através da seta do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}