package com.filipewilliam.salarium.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.fragments.GasteiFragment;
import com.filipewilliam.salarium.fragments.RecebiFragment;
import com.filipewilliam.salarium.fragments.ResumoFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button buttonResumo, buttonRecebi, buttonGastei;
    private ResumoFragment resumoFragment;
    private RecebiFragment recebiFragment;
    private GasteiFragment gasteiFragment;
    //private RecyclerView recyclerViewSaldo;
    private RecyclerView recyclerViewTransacoes; //recyclerView que cria a lista dinâmica de histórico de transações recentes do usuário na tela inicial

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setElevation(0); //Remove elevação da titleBar sobre os botões-aba dos fragments. Ou não =/.....
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        buttonResumo = findViewById(R.id.buttonResumo);
        buttonRecebi = findViewById(R.id.buttonRecebi);
        buttonGastei = findViewById(R.id.buttonGastei);
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes);

        final ResumoFragment resumoFragment = new ResumoFragment();
        final RecebiFragment recebiFragment = new RecebiFragment();
        final GasteiFragment gasteiFragment = new GasteiFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutConteudo, resumoFragment);
        transaction.commit();

        buttonResumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutConteudo, resumoFragment);
                transaction.commit();
            }
        });

        buttonRecebi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutConteudo, recebiFragment);
                transaction.commit();
            }
        });

        buttonGastei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLayoutConteudo, gasteiFragment);
                transaction.commit();
            }
        });
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
            ResumoFragment resumoFragment = new ResumoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutConteudo, resumoFragment);
            transaction.commit();
        } else if (id == R.id.nav_cadastrarSalario) {
            RecebiFragment recebiFragment = new RecebiFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutConteudo, recebiFragment);
            transaction.commit();
        } else if (id == R.id.nav_cadastrarDespesas) {
            GasteiFragment gasteiFragment = new GasteiFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutConteudo, gasteiFragment);
            transaction.commit();
        } else if (id == R.id.nav_contasVencer) {

        } else if (id == R.id.nav_definirMetas) {
            //maneira de abrir outra activity em vez de fragment
            Intent intent = new Intent(this, MetasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_configuracoes) {

        } else if (id == R.id.nav_sair) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}