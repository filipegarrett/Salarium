package com.filipewilliam.salarium.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.config.ConfiguracaoFirebase;
import com.filipewilliam.salarium.helpers.Base64Custom;
import com.filipewilliam.salarium.helpers.DateCustom;
import com.filipewilliam.salarium.helpers.FormatarValoresHelper;
import com.filipewilliam.salarium.helpers.ValoresEmReaisMaskWatcher;
import com.filipewilliam.salarium.model.Meta;
import com.filipewilliam.salarium.model.Transacao;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class Metas2Activity extends AppCompatActivity {

    private static String TAG = "Metas ";
    private String keys;
    private double valorMetas = 0.0;
    private String mesAnoDatePicker = "";
    private EditText editTextMes, editTextValorMetas;
    private Button buttonExcluirMetas, buttonSalvarMetas;
    private TextView textViewMetas, textViewMetasTotalRecebido, textViewMetasTotalGasto, textViewMetasSaldo, textViewSemDadosGrafico, textViewTituloGrafico,
            textViewSemDadosResumo, textViewSaldoTexto, textViewTotalRecebidoTexto, textViewTotalGastoTexto, textViewSaldoMesMetasTexto;
    private ProgressBar progressBarResumoMetas, progressBarGraficoMetas;
    private DatabaseReference referenciaMetas = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference referenciaAtualiza = ConfiguracaoFirebase.getFirebaseDatabase();
    private DatabaseReference referenciaTotais = ConfiguracaoFirebase.getFirebaseDatabase();
    private ValueEventListener valueEventListenerMetas;
    private ValueEventListener valueEventListenerTotais;
    private PieChart pieChartMetas;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    final String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Metas de gastos");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metas2);

        editTextMes = findViewById(R.id.editTextMesMetas);
        editTextValorMetas = findViewById(R.id.editTextValorMetas);
        editTextValorMetas.addTextChangedListener(new ValoresEmReaisMaskWatcher(editTextValorMetas));
        buttonExcluirMetas = findViewById(R.id.buttonExcluirMetas);
        buttonSalvarMetas = findViewById(R.id.buttonSalvarMetas);
        textViewSaldoTexto = findViewById(R.id.textViewSaldoMetaTexto);
        textViewTotalRecebidoTexto = findViewById(R.id.textViewTotalRecebidoMetasTexto);
        textViewTotalGastoTexto = findViewById(R.id.textViewTotalGastoMetasTexto);
        textViewSaldoMesMetasTexto = findViewById(R.id.textViewTotalSaldoMetasTexto);
        textViewMetas = findViewById(R.id.textViewMetasValor);
        textViewMetasSaldo = findViewById(R.id.textViewSaldoMetasValor);
        textViewMetasTotalRecebido = findViewById(R.id.textViewTotalRecebidoMetasValor);
        textViewMetasTotalGasto = findViewById(R.id.textViewTotalGastoMetasValor);
        textViewTituloGrafico = findViewById(R.id.textViewGraficoMetasTitulo);
        textViewSemDadosResumo = findViewById(R.id.textViewSemDadosMetas);
        textViewSemDadosGrafico = findViewById(R.id.textViewSemDadosGraficoMetas);
        progressBarResumoMetas = findViewById(R.id.progressBarDadosMetas);
        progressBarGraficoMetas = findViewById(R.id.progressBarGraficoMetas);
        pieChartMetas = findViewById(R.id.pieChartMetas);

        editTextMes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zerarVariaveis();
                final Calendar today = Calendar.getInstance();

                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(Metas2Activity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        Log.d(TAG, "mesSelecionado : " + selectedMonth + " anoSelecionado : " + selectedYear);
                        mesAnoDatePicker = (selectedMonth + 1) + String.valueOf(selectedYear);
                        editTextMes.setText(DateCustom.formatarMesAno(mesAnoDatePicker));
                        recuperaMetas(mesAnoDatePicker);
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));

                builder.setActivatedMonth(today.get(Calendar.MONTH))
                        .setMinYear(today.get(Calendar.YEAR))
                        .setActivatedYear(today.get(Calendar.YEAR))
                        .setMaxYear(2030)
                        .setMinYear(today.get(Calendar.YEAR))
                        .setTitle("Selecione o mês")
                        .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                            @Override
                            public void onMonthChanged(int selectedMonth) {
                                Log.d(TAG, "Mês selecionado : " + selectedMonth);
                                // Toast.makeText(MainActivity.this, " Selected month : " + selectedMonth, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                            @Override
                            public void onYearChanged(int selectedYear) {
                                Log.d(TAG, "Ano selecionado : " + selectedYear);
                                // Toast.makeText(MainActivity.this, " Selected year : " + selectedYear, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build()
                        .show();

            }

        });

        if (mesAnoDatePicker.isEmpty()) {
            recuperaMetas(DateCustom.retornaMesAno());
            editTextMes.setText(DateCustom.formatarMesAno(DateCustom.retornaMesAno()));

        }

        buttonSalvarMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editTextMes.getText().toString().isEmpty()) {
                    if (!editTextValorMetas.getText().toString().isEmpty()) {

                        atualizaMetas();
                        esconderTeclado();

                    } else {
                        Toast.makeText(Metas2Activity.this, "Você precisa definir um valor!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Metas2Activity.this, "Você precisa escolher um mês", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonExcluirMetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!editTextMes.getText().toString().isEmpty()) {
                    Meta.excluirMetasFirebase(mesAnoDatePicker);
                    Toast.makeText(getApplicationContext(), "Meta foi excluída com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Metas2Activity.this, "Você precisa escolher um mês!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void recuperaMetas(String mes) {

        referenciaTotais = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(idUsuario).child("transacao").child(mes);

        valueEventListenerMetas = referenciaMetas.child("usuarios").child(idUsuario).child("metas").child(mes).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {
                    progressBarResumoMetas.setVisibility(View.GONE);
                    progressBarGraficoMetas.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Meta metasFirebase = dataSnapshot1.getValue(Meta.class);
                        valorMetas = Double.valueOf(metasFirebase.valorMeta);
                        atualizaValoresTela(true);

                    }

                    valueEventListenerTotais = referenciaTotais.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            double totalDespesaMes = 0;
                            double totalRecebidoMes = 0;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                Transacao transacao = dataSnapshot1.getValue(Transacao.class);

                                if (dataSnapshot1.child("tipo").getValue().toString().equals("Gastei")) {
                                    totalDespesaMes = totalDespesaMes + transacao.getValor();

                                } else {
                                    totalRecebidoMes = totalRecebidoMes + transacao.getValor();

                                }
                                calculaTotais(totalRecebidoMes, totalDespesaMes, valorMetas);
                                geraGraficos(true, valorMetas, totalDespesaMes);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    zerarVariaveis();
                    atualizaValoresTela(false);
                    geraGraficos(false, 0, 0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void calculaTotais(double totalRecebido, double totalGasto, double valorMetas) {

        double saldoCorrente;
        double saldo;
        saldoCorrente = totalRecebido - totalGasto;

        saldo = valorMetas - saldoCorrente;

        textViewMetas.setText(FormatarValoresHelper.tratarValores(valorMetas));

        if (saldo < 0) {

            textViewMetasSaldo.setText(FormatarValoresHelper.tratarValores(saldo));
            textViewMetasSaldo.setTextColor(ContextCompat.getColor(Metas2Activity.this, R.color.corBotoesCancela));

        } else {

            textViewMetasSaldo.setText(FormatarValoresHelper.tratarValores(saldo));
            textViewMetasSaldo.setTextColor(ContextCompat.getColor(Metas2Activity.this, R.color.corBotoesConfirma));

        }

        textViewMetasTotalRecebido.setText(FormatarValoresHelper.tratarValores(totalRecebido));
        textViewMetasTotalGasto.setText(FormatarValoresHelper.tratarValores(totalGasto));

    }

    public void geraGraficos(boolean metasExistentes, double valorMetas, double totalGasto) {


        if (metasExistentes) {

            textViewSemDadosGrafico.setVisibility(View.GONE);
            textViewTituloGrafico.setVisibility(View.VISIBLE);
            pieChartMetas.setVisibility(View.VISIBLE);

            double saldoMeta = valorMetas - totalGasto;
            ArrayList dadosMetas = new ArrayList();

            if (saldoMeta > 0 && totalGasto > 0) {
                dadosMetas.add(new PieEntry((float) totalGasto, ""));
                dadosMetas.add(new PieEntry((float) saldoMeta, ""));

            }
            if (totalGasto == 0) {
                dadosMetas.add(new PieEntry((float) saldoMeta, ""));

            }

            PieDataSet dataSet = new PieDataSet(dadosMetas, "");
            PieData dados = new PieData(dataSet);

            dados.setValueFormatter(new FormatarValoresHelper());
            pieChartMetas.setData(dados);

            dataSet.setColors(new int[]{R.color.corFundoCardViewDespesa, R.color.corFundoCardViewRecebido}, Metas2Activity.this);
            dataSet.setSliceSpace(2f);
            dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
            dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
            dados.setValueTextSize(12f);
            dados.setValueTextColor(Color.WHITE);
            pieChartMetas.setMaxAngle(180f);
            pieChartMetas.setRotationAngle(180f);
            pieChartMetas.setRotationEnabled(false);
            pieChartMetas.getDescription().setEnabled(false);
            pieChartMetas.getLegend().setEnabled(false);
            pieChartMetas.setExtraOffsets(5, 5, 5, -150);
            pieChartMetas.animateX(1000);
            pieChartMetas.invalidate();

        } else {
            progressBarGraficoMetas.setVisibility(View.GONE);
            pieChartMetas.setVisibility(View.GONE);
            textViewTituloGrafico.setVisibility(View.GONE);
            textViewSemDadosGrafico.setVisibility(View.VISIBLE);
        }

    }

    public void atualizaMetas() {
        
        if(mesAnoDatePicker.isEmpty()){
            mesAnoDatePicker = DateCustom.retornaMesAno();
        }

        referenciaAtualiza.child("usuarios").child(idUsuario).child("metas").child(mesAnoDatePicker).addListenerForSingleValueEvent
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            DatabaseReference referencia = null;
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                keys = dataSnapshot1.getKey();
                                referencia = referenciaAtualiza.child("usuarios").child(idUsuario).child("metas").child(mesAnoDatePicker).child(keys).child("valorMeta");

                            }
                            Meta metas2 = new Meta();
                            metas2.atualizarMetasFirebase(referencia, Double.parseDouble(editTextValorMetas.getText().toString().replace(",", "")));
                            Toast.makeText(Metas2Activity.this, "Meta atualizada com sucesso!", Toast.LENGTH_SHORT).show();

                        } else {
                            salvarMetas();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    public void salvarMetas() {

        Meta meta = new Meta();
        meta.setMesMeta(mesAnoDatePicker);
        meta.setValorMeta(Double.parseDouble(editTextValorMetas.getText().toString().replace(",", "")));
        meta.salvarMetasFirebase(mesAnoDatePicker);
        Toast.makeText(Metas2Activity.this, "Meta salva com sucesso!", Toast.LENGTH_SHORT).show();
    }

    public void atualizaValoresTela(boolean metasExistentes) {

        if (metasExistentes) {

            textViewSemDadosResumo.setVisibility(View.GONE);
            textViewSaldoTexto.setVisibility(View.VISIBLE);
            textViewSaldoTexto.setVisibility(View.VISIBLE);
            textViewTotalRecebidoTexto.setVisibility(View.VISIBLE);
            textViewTotalGastoTexto.setVisibility(View.VISIBLE);
            textViewSaldoMesMetasTexto.setVisibility(View.VISIBLE);
            textViewMetas.setVisibility(View.VISIBLE);//setText("R$ 0.0");
            textViewMetasSaldo.setVisibility(View.VISIBLE);//setText("R$ 0.0");
            textViewMetasTotalRecebido.setVisibility(View.VISIBLE);//setText("R$ 0.0");
            textViewMetasTotalGasto.setVisibility(View.VISIBLE);//setText("R$ 0.0");
            editTextValorMetas.getText().clear();

        } else {
            progressBarResumoMetas.setVisibility(View.GONE);
            textViewSemDadosResumo.setVisibility(View.VISIBLE);
            textViewSaldoTexto.setVisibility(View.INVISIBLE);
            textViewSaldoTexto.setVisibility(View.INVISIBLE);
            textViewTotalRecebidoTexto.setVisibility(View.INVISIBLE);
            textViewTotalGastoTexto.setVisibility(View.INVISIBLE);
            textViewSaldoMesMetasTexto.setVisibility(View.INVISIBLE);
            textViewMetas.setVisibility(View.INVISIBLE);//setText("R$ 0.0");
            textViewMetasSaldo.setVisibility(View.INVISIBLE);//setText("R$ 0.0");
            textViewMetasTotalRecebido.setVisibility(View.INVISIBLE);//setText("R$ 0.0");
            textViewMetasTotalGasto.setVisibility(View.INVISIBLE);//setText("R$ 0.0");
            editTextValorMetas.getText().clear();

        }

    }

    public void zerarVariaveis() {

        valorMetas = 0.0;
    }

    public void esconderTeclado() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        referenciaMetas.removeEventListener(valueEventListenerMetas);
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
