package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.HoverMode;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipDisplayMode;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;
import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import java.util.ArrayList;
import java.util.List;

import networking_handlers.networking_statics;

public class HomeAcitvity extends AppCompatActivity {

    Button newSignalB;
    private Button mySignal;
    LoggedInUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Bundle b = getIntent().getExtras();

        if (b == null) {
            System.out.println("GET INTENT EXTRAS IS NULL");
            return;
        }
        if (!b.containsKey("user")) {
            System.out.println("Trying to start home activity without passing in a user!");
            return;
        }

        user = (LoggedInUser) b.get("user");

/**
 * //        Cartesian cartesian = AnyChart.cartesian();
 *         //LinearGauge lg = AnyChart.linear();
 *         //Pie pie = AnyChart.pie();
 *
 * //        cartesian.title("MSFT Market Summary 2019-Present");
 * //
 * //        cartesian.yAxis(0).title("Cost per share ($)");
 * //        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
 * //        cartesian.background().fill("#141D26");
 *
 *         //anyChartView.setChart(pie);
 *         //anyChartView.setChart(lg);
 *

 */

        newSignalB = findViewById(R.id.newSignals);
        mySignal = findViewById(R.id.mySignals2);
        setUpChart();

        newSignalB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewSignalActivity((LoggedInUser) b.get("user"));
            }
        });

        mySignal = findViewById(R.id.mySignals);
        mySignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMySignalsActivity((LoggedInUser) b.get("user"));
            }
        });


    }

    public List<DataEntry> getMarkerData() {
        List<DataEntry> data = new ArrayList<>();

        String[] event_dates = ((ThresholdExperiment) user.getExperiments()[0]).event_dates;
        double[] price_deltas = ((ThresholdExperiment) user.getExperiments()[0]).price_deltas;
        // TODO: implement comparator interface on ThresholdExperiment class to sort by event_dates

        for (int i = 0; i < user.getExperiments().length; ++i) {
            System.out.println(event_dates[i] + " : " + price_deltas[i]);
            data.add(new ValueDataEntry(event_dates[i], price_deltas[i]));
        }
        return data;
    }

    public void setUpChart() {
        AnyChartView anyChartView = findViewById(R.id.any_chart_view);
        Scatter scatter = AnyChart.scatter();
        scatter.animation(true);

        scatter.title("System interruptions");

        scatter.xScale()
                .minimum(1.5d)
                .maximum(5.5d);
//        scatter.xScale().tick
        scatter.yScale()
                .minimum(40d)
                .maximum(100d);

        scatter.yAxis(0).title("Waiting time between interruptions (Min)");
        scatter.xAxis(0)
                .title("Date")
                .drawFirstLabel(false)
                .drawLastLabel(false);

        scatter.interactivity()
                .hoverMode(HoverMode.BY_SPOT)
                .spotRadius(30d);

        scatter.tooltip().displayMode(TooltipDisplayMode.UNION);

        Marker marker = scatter.marker(getMarkerData());
        marker.type(MarkerType.TRIANGLE_UP)
                .size(4d);
        marker.hovered()
                .size(7d)
                .fill(new SolidFill("gold", 1d))
                .stroke("anychart.color.darken(gold)");
        marker.tooltip()
                .hAlign(HAlign.START)
                .format("Waiting time: ${%Value} min.\\nDuration: ${%X} min.");

//        Line scatterSeriesLine = scatter.line(getLineData());

//        GradientKey gradientKey[] = new GradientKey[] {
//                new GradientKey("#abcabc", 0d, 1d),
//                new GradientKey("#cbacba", 40d, 1d)
//        };
//        LinearGradientStroke linearGradientStroke = new LinearGradientStroke(0d, null, gradientKey, null, null, true, 1d, 2d);
//        scatterSeriesLine.stroke(linearGradientStroke, 3d, null, (String) null, (String) null);

        anyChartView.setChart(scatter);

    }

    public void redirectTD(View view) {
        Intent tdAmer = new Intent(Intent.ACTION_VIEW, Uri.parse(networking_statics.tdaURL));
        startActivity(tdAmer);
    }

    public void openMySignalsActivity(LoggedInUser user) {
        Intent intent = new Intent(this, MySignals.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void startNewSignalActivity(LoggedInUser user) {
        Intent intent = new Intent(this, NewSignal.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
