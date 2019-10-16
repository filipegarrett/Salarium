package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.fragments.GasteiFragment;
import com.filipewilliam.salarium.fragments.RecebiFragment;
import com.filipewilliam.salarium.fragments.ResumoFragment;
import com.filipewilliam.salarium.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;
    private TextView textViewNomeUsuario, textViewEmailUsuario;
    private RecyclerView recyclerViewTransacoes; //recyclerView que cria a lista dinâmica de histórico de transações recentes do usuário na tela inicial
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(0); //Remove elevação da titleBar sobre os botões-aba dos fragments. Ou não =/.....
        final FloatingActionButton fab = findViewById(R.id.fabAdicionarCategoria);
        fab.hide();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        textViewNomeUsuario = navigationView.getHeaderView(0).findViewById(R.id.textViewNomeUsuarioHeader);
        textViewEmailUsuario = navigationView.getHeaderView(0).findViewById(R.id.textViewEmailUsuarioHeader);

        String nomeUsuario = "";
        FirebaseUser user = autenticacao.getCurrentUser();
        if(user != null){
            nomeUsuario = user.getDisplayName();
        }

        autenticacao.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = firebaseAuth.getCurrentUser();
                if(usuario != null){
                    textViewNomeUsuario.setText(usuario.getDisplayName());
                    textViewEmailUsuario.setText(usuario.getEmail());
                }
            }
        });

        viewPager = findViewById(R.id.viewPager);
        smartTabLayout = findViewById(R.id.viewPagerTab);
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes);

        FragmentPagerItemAdapter adapterSmartTab = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Resumo", ResumoFragment.class).add("Recebi", RecebiFragment.class).add("Gastei", GasteiFragment.class).create());

        viewPager.setAdapter(adapterSmartTab);
        smartTabLayout.setViewPager(viewPager);

        //método para ocultar o FAB na fragment de resumo
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

                if (i ==0 ){
                    fab.hide();
                }else {
                    fab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

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

        } else if (id == R.id.nav_contasVencer) {
            Intent intent = new Intent(this, ContasVencerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_definirMetas) {
            //maneira de abrir outra activity em vez de fragment
            Intent intent = new Intent(this, MetasActivity.class);
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

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        final FirebaseUser usuario = autenticacao.getCurrentUser();

        if(usuario != null && autenticacao.getCurrentUser().isEmailVerified()) {

        }else{
            finishAffinity();
            System.exit(0);
        }

    }

}