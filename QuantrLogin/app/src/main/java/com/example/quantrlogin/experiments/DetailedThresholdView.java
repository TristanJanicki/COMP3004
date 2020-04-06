package com.example.quantrlogin.experiments;

import android.graphics.Color;
import android.graphics.Paint;
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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.logging.Logger;
public class DetailedThresholdView  extends Fragment {
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();
    ThresholdExperiment e;
    double[] priceDeltas;
    Button notify;
    private EditText threshTitle, threshVal;
    private ConstraintLayout detailedThreshConstraint;
    private boolean checkDarkMode;

    public DetailedThresholdView (ThresholdExperiment e){
        this.e=e;
        this.priceDeltas = e.getPrice_deltas();
    }

    private BarChart setUpChart(View view){
        BarChart barChart = view.findViewById(R.id.barchart);
        checkDarkMode = HomeAcitvity.getDarkMode();
        updateDarkMode(view);
        barChart.setHighlightPerDragEnabled(true);
        barChart.setDrawBorders(true);

        barChart.setBorderColor(getResources().getColor(android.R.color.white));


        YAxis yAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        yAxis.setDrawGridLines(false);
        rightAxis.setDrawGridLines(false);
        barChart.requestDisallowInterceptTouchEvent(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);// disable x axis grid lines
        rightAxis.setTextColor(Color.WHITE);
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawLabels(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"-10", "-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        BarDataSet set1 = new BarDataSet(getData(), "% Price Changes");

        set1.setColor(Color.rgb(80, 80, 80));
        set1.setBarBorderColor(getResources().getColor(R.color.colorPrimaryDark));
        set1.setBarBorderWidth(0.8f);
        set1.setDrawValues(true);
        BarData dat = new BarData(set1);
        Description desc = new Description();
        desc.setText("% Price Changes");
        desc.setTextSize(15);
        desc.setTextAlign(Paint.Align.RIGHT);
        barChart.setDescription(desc);
        // set data
        barChart.setData(dat);
        barChart.invalidate();
        return barChart;
    }

    ArrayList<BarEntry> getData(){
        ArrayList<BarEntry> data = new ArrayList<>();
        float[] buckets = new float[]{-10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        float[] bucketCounts = new float[buckets.length]; // the i-th element in here will be the count of how many data points from priceDeltas is within i-th and i-th + 1 element range.
        Arrays.fill(bucketCounts, 0); // fill the array with all 0's so we can do maths easily.
        // 1 entry in data for each % change from -10% to +10%
        // then we count how many price deltas are between bucket[i] and bucket[i+1]
        for (int i = 0; i < buckets.length - 1; i ++){ // this loop counts how many price deltas fall within each bucket
            for (double d : priceDeltas){
                if (d >= buckets[i] && d <= buckets[i + 1]) {
                    bucketCounts[i] ++;
                }
            }
        }

        for (int i = 0; i < buckets.length; i ++){
            data.add(new BarEntry(buckets[i], bucketCounts[i]));
        }

        return data;
    }

    String getOccurancesThisYear(){
        int occurances = 0;
        for (String s : e.getEvent_dates()){
            String[] dateParts = s.split("-");
            if (dateParts.length < 3) continue; // skip this iteration.
            String yearStr = dateParts[0];
            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
            int yearInt = Integer.parseInt(yearStr);
            if (thisYear == yearInt){
                occurances ++;
            }
        }
        return occurances + "";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_detailed_threshold_view, container, false);

        linearLayout = view.findViewById(R.id.linearLayout);
        threshTitle = view.findViewById(R.id.editText2);
        threshVal = view.findViewById(R.id.occurancesCount);

        threshVal.setText(getOccurancesThisYear());

        checkDarkMode = HomeAcitvity.getDarkMode();
        updateDarkMode(view);
        setUpChart(view);

        notify = view.findViewById(R.id.button);
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Available in a future update.", Toast.LENGTH_SHORT).show();
            }
        });

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