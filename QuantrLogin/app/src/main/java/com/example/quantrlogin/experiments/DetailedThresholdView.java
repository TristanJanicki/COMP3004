package com.example.quantrlogin.experiments;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;
import com.example.quantrlogin.ui.login.HomeAcitvity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
public class DetailedThresholdView  extends Fragment {
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();
    ThresholdExperiment e;
    public DetailedThresholdView (ThresholdExperiment e){
        this.e=e;
    }
    double[] priceDeltas;
    Button notify;
    private EditText threshTitle, threshVal;
    private ConstraintLayout detailedThreshConstraint;
    private boolean checkDarkMode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_detailed_threshold_view, container, false);

        linearLayout = view.findViewById(R.id.linearLayout);
        threshTitle = view.findViewById(R.id.editText2);
        threshVal = view.findViewById(R.id.editText3);
        BarChart barChart = view.findViewById(R.id.barchart);

        checkDarkMode = HomeAcitvity.getDarkMode();
        updateDarkMode(view);

        barChart.setHighlightPerDragEnabled(true);
        priceDeltas=e.getPrice_deltas();
        Logger.getGlobal().warning(Arrays.toString(e.getPrice_deltas()));
        barChart.setDrawBorders(true);

        barChart.setBorderColor(getResources().getColor(android.R.color.white));

        YAxis yAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        barChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setDrawGridLines(false);// disable x axis grid lines
        xAxis.setDrawLabels(false);
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);

        Legend l = barChart.getLegend();
        l.setEnabled(false);
        ArrayList<BarEntry> xValsBar= new ArrayList<BarEntry>();
        double x=-1/3;
        Arrays.sort(priceDeltas);
        //int h= (int) (2*IQR(priceDeltas,priceDeltas.length)*(Math.pow(priceDeltas.length,x)));
        int bins=(int)Math.ceil(Math.sqrt(priceDeltas.length));
        int h=(int)(priceDeltas[priceDeltas.length-1]-priceDeltas[0])/bins;
        ArrayList data = new ArrayList();
        bins=bins*2;
        Logger.getGlobal().warning("Bin width:" +h);
        Logger.getGlobal().warning(" deltas length:" + priceDeltas.length);
        Logger.getGlobal().warning("bins:" + bins);
        int temp=-bins/2;
        int curr=0;
        for(int i=0;i<bins;i++){
            int occur=0;
            for(int a=curr;a<priceDeltas.length;a++){
                if(temp+1>=priceDeltas[curr]){
                    occur+=1;
                }
                else{
                    temp+=1;
                }
                curr+=1;
            }
            data.add(new BarEntry(occur,i));
        }
        ArrayList xAx = new ArrayList();
        for(int i=-bins/2;i<bins+1;i++){
            xAx.add(i);
        }


        BarDataSet set1 = new BarDataSet(data, "Data");
        set1.setColor(Color.rgb(80, 80, 80));
        set1.setBarBorderColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setBarBorderWidth(0.8f);
        set1.setDrawValues(false);

        notify = view.findViewById(R.id.button);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Available in a future update.", Toast.LENGTH_SHORT).show();
            }
        });


// create a data object with the datasets
        BarData dat = new BarData((IBarDataSet) xAx, set1);


// set data
        barChart.setData(dat);
        barChart.invalidate();
        return view;
    }
    public int median(double a[], int l, int r) {
        int n = r - l + 1;
        n = (n + 1) / 2 - 1;
        return n + l;
    }


    public double IQR(double [] a, int n) {
        int mid_index = median(a, 0, n);
        double Q1 = a[median(a, 0, mid_index)];
        double Q3 = a[median(a, mid_index + 1, n)];
        return (Q3 - Q1);
    }

    public void updateDarkMode(View view) {
        detailedThreshConstraint = view.findViewById(R.id.detailedThresholdView);

        if (checkDarkMode) { //if in light mode
            //make necessary changes to convert to dark mode
            detailedThreshConstraint.setBackgroundColor(getResources().getColor(R.color.DarkNavy));
            threshTitle.setTextColor(getResources().getColor(R.color.LightGrey));
            threshVal.setTextColor(getResources().getColor(R.color.LightGrey));
            threshTitle.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            threshVal.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));

        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            detailedThreshConstraint.setBackgroundColor(getResources().getColor(R.color.White));
            threshTitle.setTextColor(getResources().getColor(R.color.Grey));
            threshVal.setTextColor(getResources().getColor(R.color.Grey));
            threshTitle.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            threshVal.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
        }
    }
}