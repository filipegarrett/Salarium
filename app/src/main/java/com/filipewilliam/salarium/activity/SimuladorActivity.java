package com.filipewilliam.salarium.activity;

import android.icu.text.NumberFormat;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;

public class SimuladorActivity extends AppCompatActivity {

    private Spinner spinnerTiposIvestimentos;
    private TextView textViewMesesSimulados, textViewResultadoTexto, textViewResultadoSimulacao, textViewRendimentoTexto, textViewResultadoRendimento;
    private Button buttonLimparCampos, buttonCalcularSimulacao;
    private SeekBar seekBarQuantidadeMeses;
    private EditText editTextValorSimulacao;
    int quantidadeMeses;
    private double taxaPoupanca, taxaTesouro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Simulador de Investimentos");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAjuda);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        spinnerTiposIvestimentos = findViewById(R.id.spinnerTipoInvestimento);
        textViewMesesSimulados = findViewById(R.id.textViewMesesSimulacao);
        textViewResultadoTexto = findViewById(R.id.textViewResultadoTexto);
        textViewResultadoSimulacao = findViewById(R.id.textViewResultadoSimulacao);
        textViewRendimentoTexto = findViewById(R.id.textViewRendimentoTexto);
        textViewResultadoRendimento = findViewById(R.id.textViewRendimento);
        buttonLimparCampos = findViewById(R.id.buttonLimparCampos);
        buttonCalcularSimulacao = findViewById(R.id.buttonCalcularInvestimento);
        seekBarQuantidadeMeses = findViewById(R.id.seekBarPeriodoSimulacao);
        editTextValorSimulacao = findViewById(R.id.editTextValorSimulacao);
        editTextValorSimulacao.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorSimulacao));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.arrayTiposInvestimentos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTiposIvestimentos.setAdapter(adapter);

        seekBarQuantidadeMeses.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewMesesSimulados.setText(seekBarQuantidadeMeses.getProgress() + " meses");
                quantidadeMeses = seekBarQuantidadeMeses.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonCalcularSimulacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinnerTiposIvestimentos.getSelectedItem().toString().equals("Poupança")){
                    simularPoupanca();

                }else{
                    simularTesouro();
                }

            }
        });

        buttonLimparCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limparCampos();
            }
        });
    }

    public void simularPoupanca(){
        System.out.println(editTextValorSimulacao.getText().toString());
        double valorSimulacao = Double.valueOf(editTextValorSimulacao.getText().toString().replace(",", ""));
        System.out.println(valorSimulacao);
        double resultado = 0;
        taxaPoupanca = 0.003434;

        resultado = valorSimulacao + ((valorSimulacao * taxaPoupanca) * quantidadeMeses);
        System.out.println(resultado);
        double rendimento = resultado - valorSimulacao;
        String rendimentoString = String.valueOf(NumberFormat.getCurrencyInstance().format(rendimento));
        String rendimentoTratado = resultadoEmReais(rendimentoString);
        String resultadoString = String.valueOf(NumberFormat.getCurrencyInstance().format(resultado));
        String resultadoTratado = resultadoEmReais(resultadoString);

        textViewResultadoTexto.setText("Ao final você terá: ");
        textViewRendimentoTexto.setText("O rendimento no período é de: ");
        textViewResultadoSimulacao.setText(resultadoTratado);
        textViewResultadoRendimento.setText(rendimentoTratado);
        editTextValorSimulacao.setText("");
        seekBarQuantidadeMeses.setProgress(0);

    }

    public void simularTesouro(){
        textViewResultadoSimulacao.setText("AHÁ!");
    }

    public void limparCampos(){
        textViewMesesSimulados.setText("meses");
        textViewResultadoSimulacao.setText("");
        textViewResultadoRendimento.setText("");
        seekBarQuantidadeMeses.setProgress(0);
        editTextValorSimulacao.setText("");

    }

    public String resultadoEmReais(String valor){
        String valorTratado = "";
        String valorOriginal = String.valueOf(valor);
        int indice = 1;

        for (int i = 0; i < valorOriginal.length(); i++) {
            valorTratado += valorOriginal.charAt(i);

            if (i == indice) {
               valorTratado += " ";
            }
        }
        return valorTratado;

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
