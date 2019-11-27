package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.fragments.GasteiFragment;
import com.filipewilliam.salarium.fragments.RecebiFragment;
import com.filipewilliam.salarium.fragments.ResumoFragment;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.model.Usuario;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;
    private int tipoDestino;
    private TextView textViewNomeUsuario, textViewEmailUsuario;
    private RecyclerView recyclerViewTransacoes; //recyclerView que cria a lista dinâmica de histórico de transações recentes do usuário na tela inicial
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(0); //Remove elevação da titleBar sobre os botões-aba dos fragments. Ou não =/.....
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        textViewNomeUsuario = navigationView.getHeaderView(0).findViewById(R.id.textViewNomeUsuarioHeader);
        textViewEmailUsuario = navigationView.getHeaderView(0).findViewById(R.id.textViewEmailUsuarioHeader);

        autenticacao.getCurrentUser().reload();
        verificarUsuarioLogado();

        DatabaseReference referenciaEmail = ConfiguracaoFirebase.getFirebaseDatabase();
        referenciaEmail.keepSynced(false);
        referenciaEmail.child("usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usuarios : dataSnapshot.getChildren()) {
                    Usuario usuario = usuarios.getValue(Usuario.class);
                    if (autenticacao.getCurrentUser().getEmail().equals(Base64Custom.decodificarBase64(usuarios.getKey()))) {
                        textViewNomeUsuario.setText(usuario.getNome());
                        textViewEmailUsuario.setText(Base64Custom.decodificarBase64(usuarios.getKey()));
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewPager = findViewById(R.id.viewPager);
        smartTabLayout = findViewById(R.id.viewPagerTab);
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes);

        FragmentPagerItemAdapter adapterSmartTab = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Resumo", ResumoFragment.class).add("Recebi", RecebiFragment.class).add("Gastei", GasteiFragment.class).create());

        viewPager.setAdapter(adapterSmartTab);
        smartTabLayout.setViewPager(viewPager);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tipoDestino = extras.getInt("DESTINO");

            if(tipoDestino == 2){
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                GasteiFragment fragment = new GasteiFragment();
                fragmentTransaction.add(R.id.viewPager, fragment);
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        verificarUsuarioLogado();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_resumo) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contasVencer) {
            Intent intent = new Intent(this, ContasVencerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_definirMetas) {
            //maneira de abrir outra activity em vez de fragment
            Intent intent = new Intent(this, Metas2Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_simularPoupanca) {
            Intent intent = new Intent(this, SimuladorActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_gerarRelatorios) {
            Intent intent = new Intent(this, RelatoriosActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_configuracoes) {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sair) {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signOut();
            finishAffinity();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            autenticacao.removeAuthStateListener(mAuthListener);

        }
    }

    public void verificarUsuarioLogado() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final FirebaseUser firebaseUsuario = autenticacao.getCurrentUser();
        firebaseUsuario.reload();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseUsuario != null) {

                } else {
                    finishAffinity();
                    System.exit(0);

                }

            }

        };

    }

}