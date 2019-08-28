package com.filipewilliam.salarium.activity;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.fragments.GasteiFragment;
import com.filipewilliam.salarium.fragments.RecebiFragment;
import com.filipewilliam.salarium.fragments.ResumoFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //private Button buttonResumo, buttonRecebi, buttonGastei;
    private ResumoFragment resumoFragment;
    private RecebiFragment recebiFragment;
    private GasteiFragment gasteiFragment;
    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;
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

        viewPager = findViewById(R.id.viewPager);
        smartTabLayout = findViewById(R.id.viewPagerTab);
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes);

        final ResumoFragment resumoFragment = new ResumoFragment();
        final RecebiFragment recebiFragment = new RecebiFragment();
        final GasteiFragment gasteiFragment = new GasteiFragment();

        /*FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutConteudo, resumoFragment);
        transaction.commit();*/

        FragmentPagerItemAdapter adapterSmartTab = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Resumo", ResumoFragment.class).add("Recebi", RecebiFragment.class).add("Gastei", GasteiFragment.class).create());

        viewPager.setAdapter(adapterSmartTab);
        smartTabLayout.setViewPager(viewPager);

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

        } else if (id == R.id.nav_cadastrarSalario) {
            /*RecebiFragment recebiFragment = new RecebiFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.viewPager, recebiFragment);
            transaction.commit();*/
        } else if (id == R.id.nav_cadastrarDespesas) {
            /*GasteiFragment gasteiFragment = new GasteiFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frameLayoutConteudo, gasteiFragment);
            transaction.commit();*/
        } else if (id == R.id.nav_contasVencer) {

        } else if (id == R.id.nav_definirMetas) {

        } else if (id == R.id.nav_configuracoes) {

        } else if (id == R.id.nav_sair) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}