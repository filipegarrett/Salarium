package com.filipewilliam.salarium.activity;

import android.content.Context;
import android.icu.text.NumberFormat;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.helpers.InvestimentosHelper;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimuladorActivity extends AppCompatActivity {

    private Spinner spinnerTiposIvestimentos;
    private TextView textViewMesesSimulados, textViewResultadoTexto, textViewResultadoSimulacao, textViewRendimentoTexto, textViewResultadoRendimento, textViewPeriodoCDB;
    private Button buttonLimparCampos, buttonCalcularSimulacao;
    private SeekBar seekBarQuantidadeMeses;
    private EditText editTextValorSimulacao, editTextValorDepositoMensal;
    int quantidadeMeses, i;
    private double taxaPoupanca, taxaTesouro, valorSimulacao, valorOriginal, depositoMensal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Simulador");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonAjuda);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //estruturar ajuda/guia a respeito de como investimentos funcionam e são calculados dentro do app

            }
        });

        spinnerTiposIvestimentos = findViewById(R.id.spinnerTipoInvestimento);
        textViewMesesSimulados = findViewById(R.id.textViewMesesSimulacao);
        textViewResultadoTexto = findViewById(R.id.textViewResultadoTexto);
        textViewResultadoSimulacao = findViewById(R.id.textViewResultadoSimulacao);
        textViewRendimentoTexto = findViewById(R.id.textViewRendimentoTexto);
        textViewResultadoRendimento = findViewById(R.id.textViewRendimento);
        textViewPeriodoCDB = findViewById(R.id.textViewPeriodoCDBPrefixado);
        buttonLimparCampos = findViewById(R.id.buttonLimparCampos);
        buttonCalcularSimulacao = findViewById(R.id.buttonCalcularInvestimento);
        seekBarQuantidadeMeses = findViewById(R.id.seekBarPeriodoSimulacao);
        textViewMesesSimulados.setText("1 mês");
        editTextValorSimulacao = findViewById(R.id.editTextValorSimulacao);
        editTextValorSimulacao.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorSimulacao));
        editTextValorDepositoMensal = findViewById(R.id.editTextValorDepositoMensal);
        editTextValorDepositoMensal.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorDepositoMensal));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.arrayTiposInvestimentos, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTiposIvestimentos.setAdapter(adapter);
        spinnerTiposIvestimentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerTiposIvestimentos.getSelectedItem().toString().equals("Poupança")){
                    seekBarQuantidadeMeses.setEnabled(true);
                    editTextValorDepositoMensal.setEnabled(true);
                    textViewMesesSimulados.setText(" meses");
                    textViewPeriodoCDB.setText("");

                }else{
                    seekBarQuantidadeMeses.setEnabled(false);
                    editTextValorDepositoMensal.setEnabled(false);
                    textViewMesesSimulados.setText("");
                    textViewPeriodoCDB.setText("");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        seekBarQuantidadeMeses.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(seekBarQuantidadeMeses.getProgress() == 1){
                    textViewMesesSimulados.setText(seekBarQuantidadeMeses.getProgress() + " mês");

                }else{
                    textViewMesesSimulados.setText(seekBarQuantidadeMeses.getProgress() + " meses");

                }
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

                    if(editTextValorDepositoMensal.getText().toString().isEmpty()){
                        editTextValorDepositoMensal.setText("0,0");
                        simularPoupanca();
                    }else{
                        simularPoupanca();
                    }

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

        if(editTextValorSimulacao.getText().toString().isEmpty()){
            Toast.makeText(SimuladorActivity.this, "Por favor, insira um valor para a simulação", Toast.LENGTH_SHORT).show();

        }else{
            valorSimulacao = Double.valueOf(editTextValorSimulacao.getText().toString().replace(",", ""));
            valorOriginal = valorSimulacao;
            depositoMensal = Double.valueOf(editTextValorDepositoMensal.getText().toString().replace(",", ""));
            taxaPoupanca = 0.003434;

            InvestimentosHelper investimentosHelper = new InvestimentosHelper();
            double resultadoSimulacao = investimentosHelper.simularPoupanca(valorSimulacao, depositoMensal, taxaPoupanca, quantidadeMeses);
            String rendimento = investimentosHelper.rendimentoPoupanca(resultadoSimulacao, valorOriginal, depositoMensal, quantidadeMeses);
            String resultadoTratado = investimentosHelper.tratarValores(resultadoSimulacao);

            textViewResultadoTexto.setText("Ao final você terá: ");
            textViewRendimentoTexto.setText("O rendimento no período é: ");
            textViewResultadoSimulacao.setText(resultadoTratado);
            textViewResultadoRendimento.setText(rendimento);
            editTextValorSimulacao.setText("");
            seekBarQuantidadeMeses.setProgress(0);
            esconderTeclado();
            zerarVariaveis();

        }

    }

    public void simularTesouro(){

        if(editTextValorSimulacao.getText().toString().isEmpty()){
            Toast.makeText(SimuladorActivity.this, "Por favor, insira um valor para a simulação", Toast.LENGTH_SHORT).show();

        }else{
            valorSimulacao = Double.valueOf(editTextValorSimulacao.getText().toString().replace(",", ""));
            valorOriginal = valorSimulacao;

            InvestimentosHelper investimentosHelper = new InvestimentosHelper();
            double resultadoSimulacao = investimentosHelper.simularCDBPrefixado(valorSimulacao);
            String rendimento = investimentosHelper.rendimentoPoupanca(resultadoSimulacao, valorOriginal, depositoMensal, quantidadeMeses);
            String resultadoTratado = investimentosHelper.tratarValores(resultadoSimulacao);

            Calendar cal = Calendar.getInstance();
            Date hoje = cal.getTime();
            cal.add(Calendar.YEAR, 5);
            Date futuro = cal.getTime();
            SimpleDateFormat dataInvestimento = new SimpleDateFormat("dd-MM-yyyy");
            String data = dataInvestimento.format(futuro).replace("-", "/");

            textViewPeriodoCDB.setText("Simulação até " + data);
            textViewResultadoTexto.setText("Em " + data + " você terá: ");
            textViewRendimentoTexto.setText("O rendimento no período é: ");
            textViewResultadoSimulacao.setText(resultadoTratado);
            textViewResultadoRendimento.setText(rendimento);
            editTextValorSimulacao.setText("");
            seekBarQuantidadeMeses.setProgress(0);
            esconderTeclado();
            zerarVariaveis();

        }

    }

    public void limparCampos(){
        textViewMesesSimulados.setText("meses");
        textViewResultadoTexto.setText("");
        textViewRendimentoTexto.setText("");
        textViewResultadoSimulacao.setText("");
        textViewResultadoRendimento.setText("");
        textViewPeriodoCDB.setText("");
        seekBarQuantidadeMeses.setProgress(0);
        editTextValorSimulacao.setText("");
        editTextValorDepositoMensal.setText("");

    }

    public void zerarVariaveis(){
        valorOriginal = 0;
        valorSimulacao = 0;
        quantidadeMeses = 0;
        depositoMensal = 0;

    }

    public void esconderTeclado(){ /*garante que o teclado é ocultado, não diga, depois de clicar no botão de calcular, evitando que ele fique ativo na tela sem necessidade
        e obstruindo outros componentes*/

        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch(Exception ignored) {
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
