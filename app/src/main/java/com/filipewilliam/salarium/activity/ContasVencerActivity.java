package com.filipewilliam.salarium.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.helpers.DatasMaskWatcher;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;

public class ContasVencerActivity extends AppCompatActivity {

    private Spinner spinnerCategoriaContas;
    private Button buttonLimparCamposContasVencer, buttonCadastrarContasVencer;
    private EditText editTextValorContasVencer, editTextDataVencimentoContasVencer;
    private Switch switchVencimentoRecorrente, switchEmitirNotificacaoVencimento;
    DatasMaskWatcher maskWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Próximas despesas");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_vencer);

        spinnerCategoriaContas = findViewById(R.id.spinnerCategoriaContasVencer);
        editTextValorContasVencer = findViewById(R.id.editTextValorContasVencer);
        editTextValorContasVencer.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorContasVencer));
        editTextDataVencimentoContasVencer = findViewById(R.id.editTextDataVencimentoContaVencer);
        editTextDataVencimentoContasVencer.addTextChangedListener(maskWatcher.aplicarMaskDataNascimento());
        switchVencimentoRecorrente = findViewById(R.id.switchDespesaRecorrente);
        switchEmitirNotificacaoVencimento = findViewById(R.id.switchPermitirNotificacoes);
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
