package com.filipewilliam.salarium.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.filipewilliam.salarium.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumoFragment extends Fragment {


    public ResumoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resumo, container, false);
    }

}
