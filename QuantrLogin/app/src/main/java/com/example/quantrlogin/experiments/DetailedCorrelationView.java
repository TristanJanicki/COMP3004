package com.example.quantrlogin.experiments;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_detailed_correlation_view, container, false);
        ((TextView)view.findViewById(R.id.editText3)).setText(String.valueOf(e.getCorrelation()));
        linearLayout = view.findViewById(R.id.linearLayout);
        float correlation= e.getCorrelation();

       // ArrayList<CandleEntry> yValsCandleStick= new ArrayList<CandleEntry>();

        priceDeltas=e.getAsset_1_deltas();
        priceDeltas1=e.getAsset_2_deltas();
        ArrayList<Entry> entries= new ArrayList<>();
        for(int i=0;i<priceDeltas.length;i++){

            entries.add(new Entry(priceDeltas[i],priceDeltas1[i]));
        }

        scatterChart = (ScatterChart) view.findViewById(R.id.scatterChart);
        ScatterDataSet scatterDataSet = new ScatterDataSet(entries, e.getAsset_1());
        scatterDataSet.setColors(Color.blue(1));
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

}