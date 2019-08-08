package com.filipewilliam.salarium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filipewilliam.salarium.R;
import com.filipewilliam.salarium.adapter.AdapterTransacoes;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {

    private RecyclerView recyclerViewTransacoes;
    private AdapterTransacoes adapterTransacoes;

    public ResumoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_resumo, container, false);
        View view = inflater.inflate(R.layout.fragment_resumo, container, false);

        recyclerViewTransacoes = (RecyclerView) view.findViewById(R.id.recyclerViewTransacoes);

        AdapterTransacoes adapterTransacoes = new AdapterTransacoes();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewTransacoes.setLayoutManager(layoutManager);
        recyclerViewTransacoes.setHasFixedSize(true);
        recyclerViewTransacoes.setAdapter(adapterTransacoes);

        return view;

    }

}