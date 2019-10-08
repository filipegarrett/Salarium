package com.filipewilliam.salarium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filipewilliam.salarium.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraficosFragment extends Fragment {

    private PieChart pieChart;


    public GraficosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graficos, container, false);

        pieChart = view.findViewById(R.id.pieChartRelatorio);

        PieDataSet pieDataSet = new PieDataSet(dataValues1(),"");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();

        return view;
    }

    private ArrayList<PieEntry> dataValues1(){

        ArrayList<PieEntry> dataValues = new ArrayList<>();
        dataValues.add(new PieEntry(15, "Lanche"));
        dataValues.add(new PieEntry(285, "Aluguel"));
        dataValues.add(new PieEntry(15, "Lanche"));
        dataValues.add(new PieEntry(15, "Lanche"));
        dataValues.add(new PieEntry(15, "Lanche"));
        dataValues.add(new PieEntry(15, "Lanche"));
        dataValues.add(new PieEntry(150, "Teste"));
        return dataValues;

    }

}
