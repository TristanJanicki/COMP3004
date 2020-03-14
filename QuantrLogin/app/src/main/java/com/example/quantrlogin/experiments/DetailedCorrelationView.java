package com.example.quantrlogin.experiments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;

import java.util.logging.Logger;

public class DetailedCorrelationView extends Fragment {
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();

    public DetailedCorrelationView (){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_my_signals, container, false);

        linearLayout = view.findViewById(R.id.linearLayout);


        return view;
    }
}
