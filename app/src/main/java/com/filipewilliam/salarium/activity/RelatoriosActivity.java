package com.filipewilliam.salarium.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.fragments.GraficosFragment;
import com.filipewilliam.salarium.fragments.RelatoriosFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class RelatoriosActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SmartTabLayout smartTabLayout;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Seu desempenho");
        setContentView(R.layout.activity_relatorios);

        viewPager = findViewById(R.id.viewPagerRelatorios);
        smartTabLayout = findViewById(R.id.viewPagerTabRelatorios);

        FragmentPagerItemAdapter adapterSmartTab = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Relatórios", RelatoriosFragment.class).add("Gráficos", GraficosFragment.class).create());

        viewPager.setAdapter(adapterSmartTab);
        smartTabLayout.setViewPager(viewPager);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(this, "Permissão negada =(", Toast.LENGTH_SHORT).show();
                }
                break;
            default:

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

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
