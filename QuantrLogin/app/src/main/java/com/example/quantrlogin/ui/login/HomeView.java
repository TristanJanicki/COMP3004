package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.example.quantrlogin.R;

import java.util.ArrayList;
import java.util.List;

public class HomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button button_mySignals = findViewById(R.id.mySignals);

        Cartesian cartesian = AnyChart.cartesian();
        //LinearGauge lg = AnyChart.linear();
        //Pie pie = AnyChart.pie();

        cartesian.title("MSFT Market Summary 2019-Present");

        cartesian.yAxis(0).title("Cost per share ($)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.background().fill("#141D26");

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Mar", 112.6));
        data.add(new ValueDataEntry("Apr", 119.02));
        data.add(new ValueDataEntry("May", 127.88));
        data.add(new ValueDataEntry("June", 119.84));
        data.add(new ValueDataEntry("July", 135.68));
        data.add(new ValueDataEntry("Aug", 138.06));
        data.add(new ValueDataEntry("Sept", 136.04));
        data.add(new ValueDataEntry("Oct", 137.07));
        data.add(new ValueDataEntry("Nov", 143.72));
        data.add(new ValueDataEntry("Dec", 149.55));
        data.add(new ValueDataEntry("Jan", 160.62));
        data.add(new ValueDataEntry("Feb", 174.38));
        data.add(new ValueDataEntry("Mar", 167.29));

        //pie.data(data);
        //lg.data(data);
        cartesian.data(data);

        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        //anyChartView.setChart(pie);
        //anyChartView.setChart(lg);
        anyChartView.setChart(cartesian);

        button_mySignals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMySignalsActivity();
            }
        });
    }

    public void openMySignalsActivity() {
        Intent intent = new Intent(this, MySignals.class);
        startActivity(intent);
    }

}
