package com.example.quantrlogin.experiments;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

public class DetailedThresholdView  extends Fragment {
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();
    ThresholdExperiment e;
    public DetailedThresholdView (ThresholdExperiment e){
        this.e=e;
    }
    double[] priceDeltas;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_detailed_threshold_view, container, false);

        linearLayout = view.findViewById(R.id.linearLayout);

        CandleStickChart candleStickChart = view.findViewById(R.id.candle_stick);
        candleStickChart.setHighlightPerDragEnabled(true);
        priceDeltas=e.getPrice_deltas();
        candleStickChart.setDrawBorders(true);

        candleStickChart.setBorderColor(getResources().getColor(android.R.color.white));

        YAxis yAxis = candleStickChart.getAxisLeft();
        YAxis rightAxis = candleStickChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        candleStickChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = candleStickChart.getXAxis();

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = candleStickChart.getLegend();
        l.setEnabled(false);
        ArrayList<CandleEntry> yValsCandleStick= new ArrayList<CandleEntry>();

        for(int i=0;i<priceDeltas.length;i++){
            Logger.getGlobal().warning("Each Delta " + priceDeltas[i]);
            yValsCandleStick.add(new CandleEntry(i, 225, 219, 0, Math.round(priceDeltas[i])));
        }



        CandleDataSet set1 = new CandleDataSet(yValsCandleStick, "DataSet 1");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setShadowColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setShadowWidth(0.8f);
        set1.setDecreasingColor(getResources().getColor(android.R.color.holo_green_dark));
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(getResources().getColor(R.color.colorAccent));
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setNeutralColor(Color.LTGRAY);
        set1.setDrawValues(false);



// create a data object with the datasets
        CandleData data = new CandleData(set1);


// set data
        candleStickChart.setData(data);
        candleStickChart.invalidate();
        return view;
    }

}