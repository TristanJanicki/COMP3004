package com.example.quantrlogin.experiments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.ui.login.HomeAcitvity;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.logging.Logger;

public class DetailedCorrelationView extends Fragment {
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();
    CorrelationExperiment e;
    public DetailedCorrelationView(CorrelationExperiment e){
        this.e=e;
    }
    float[] priceDeltas;
    float[] priceDeltas1;
    private ScatterChart scatterChart;
    private EditText correlTitle, correlVal;
    private ConstraintLayout detailedCorrelConstraint;
    private boolean checkDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detailed_correlation_view, container, false);
        ((TextView)view.findViewById(R.id.editText3)).setText(String.valueOf(e.getCorrelation()));
        linearLayout = view.findViewById(R.id.linearLayout);
        correlTitle = view.findViewById(R.id.editText2);
        correlVal = view.findViewById(R.id.editText3);
        float correlation= e.getCorrelation();

        checkDarkMode = HomeAcitvity.getDarkMode();
        updateDarkMode(view);

       // ArrayList<CandleEntry> yValsCandleStick= new ArrayList<CandleEntry>();

        priceDeltas=e.getAsset_1_deltas();
        priceDeltas1=e.getAsset_2_deltas();
//        ArrayList<Entry> entries= new ArrayList<>();
//        for(int i=0;i<priceDeltas.length;i++){
//            entries.add(new Entry(priceDeltas[i],priceDeltas1[i]));
//        }

        scatterChart = (ScatterChart) view.findViewById(R.id.scatterChart);
        ScatterDataSet scatterDataSet = new ScatterDataSet(getData(), e.getAsset_1());
        scatterDataSet.setColors(R.color.Mint);

        ScatterData scatterData = new ScatterData(scatterDataSet);
        XAxis xAxis = scatterChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] deltas = new String[]{"0", ".15", ".30", ".45",".60",".75", ".90" , "1"};
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(deltas);
        xAxis.setGranularity(.15f);
        xAxis.setValueFormatter(formatter);
        scatterChart.setData(scatterData);
        scatterChart.animateXY(5000, 5000);
        scatterChart.invalidate();
        return view;
    }

    public ArrayList getData() {
        ArrayList<Entry> entries= new ArrayList<>();
        for(int i=0; i<priceDeltas.length; i++){
            entries.add(new Entry(priceDeltas[i],priceDeltas1[i]));
        }
        return entries;
    }

    public void updateDarkMode(View view) {
        detailedCorrelConstraint = view.findViewById(R.id.detailedCorrelationView);

        if (checkDarkMode) { //if in light mode
            //make necessary changes to convert to dark mode
            detailedCorrelConstraint.setBackgroundColor(getResources().getColor(R.color.DarkNavy));
            correlTitle.setTextColor(getResources().getColor(R.color.LightGrey));
            correlVal.setTextColor(getResources().getColor(R.color.LightGrey));
            correlTitle.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            correlVal.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));

        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            detailedCorrelConstraint.setBackgroundColor(getResources().getColor(R.color.White));
            correlTitle.setTextColor(getResources().getColor(R.color.Grey));
            correlVal.setTextColor(getResources().getColor(R.color.Grey));
            correlTitle.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            correlVal.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
        }
    }

}