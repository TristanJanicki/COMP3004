package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.experiments.MySignals;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
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

import networking_handlers.networking_statics;

public class HomeAcitvity extends Fragment {

    private Button newSignalB;
    private Button mySignal;
    private Button newSignal;
    private Button signalCalender;
    private LoggedInUser user;

    private LineChart lineChart;

    //private DrawerLayout drawer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);
        user = (LoggedInUser) getActivity().getIntent().getSerializableExtra("user");

        mySignal = view.findViewById(R.id.mySignals);
        mySignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySignals mySignals_fragment = new MySignals();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, mySignals_fragment, mySignals_fragment.getTag())
                        .commit();
            }
        });

//        newSignal = view.findViewById(R.id.newSignals);
//        newSignal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NewSignal.class);
//                startActivity(intent);
//            }
//        });

        signalCalender = view.findViewById(R.id.signalCalender);
        signalCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Available in a future update.", Toast.LENGTH_SHORT).show();
            }
        });

        lineChart = view.findViewById(R.id.home_chart_view);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        //ArrayList<Entry> dataValues = new ArrayList<>();

        LineDataSet homeSet = new LineDataSet(getData(), "Signal 1");
        homeSet.setColor(ContextCompat.getColor(view.getContext(), R.color.GoldYellow));
        homeSet.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.White));

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[(int) value];
            }
        };

        xAxis.setGranularity(1f);
        xAxis.setTextColor(ContextCompat.getColor(view.getContext(), R.color.White));
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setTextColor(ContextCompat.getColor(view.getContext(), R.color.White));
        yAxis.setGranularity(1f);

        LineData data = new LineData(homeSet);
        lineChart.setData(data);
        lineChart.invalidate();



//        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
//            homeSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
//            homeSet.setValues(dataValues);
//            lineChart.getData().notifyDataChanged();
//            lineChart.notifyDataSetChanged();
//        } else {
//
//        }

//        for (int i=0; i < user.experiments.length; i++){
//            dataValues.add(new Entry(i, user.experiments));
//        }



        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

        //need to do this in onCreateView probably?
//        final Bundle b = getIntent().getExtras();
//
//        if (b == null){
//            Logger.getGlobal().warning("GET INTENT EXTRAS IS NULL");
//            return;
//        }
//        if (!b.containsKey("user")){
//            Logger.getGlobal().warning("Trying to start home activity without passing in a user!");
//            return;
//        }

        //need to find a better way to display charts
//        Cartesian cartesian = AnyChart.cartesian();
//        //LinearGauge lg = AnyChart.linear();
//        //Pie pie = AnyChart.pie();
//
//        cartesian.title("MSFT Market Summary 2019-Present");
//
//        cartesian.yAxis(0).title("Cost per share ($)");
//        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
//        cartesian.background().fill("#141D26");
//
//        List<DataEntry> data = new ArrayList<>();
//        data.add(new ValueDataEntry("Mar", 112.6));
//        data.add(new ValueDataEntry("Apr", 119.02));
//        data.add(new ValueDataEntry("May", 127.88));
//        data.add(new ValueDataEntry("June", 119.84));
//        data.add(new ValueDataEntry("July", 135.68));
//        data.add(new ValueDataEntry("Aug", 138.06));
//        data.add(new ValueDataEntry("Sept", 136.04));
//        data.add(new ValueDataEntry("Oct", 137.07));
//        data.add(new ValueDataEntry("Nov", 143.72));
//        data.add(new ValueDataEntry("Dec", 149.55));
//        data.add(new ValueDataEntry("Jan", 160.62));
//        data.add(new ValueDataEntry("Feb", 174.38));
//        data.add(new ValueDataEntry("Mar", 167.29));
//
//        cartesian.data(data);
//
//        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);


        //in the drawer now, but drawer doesn't carry user tags yet
//        newSignalB = findViewById(R.id.newSignals);
//        //mySignal = findViewById(R.id.mySignals2);
//
//        newSignalB.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startNewSignalActivity((LoggedInUser) b.get("user"));
//            }
//        });

//        mySignal = findViewById(R.id.mySignals);
//        mySignal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openMySignalsActivity((LoggedInUser) b.get("user"));
//            }
//        });

        //anyChartView.setChart(cartesian);

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

    private ArrayList getData(){
        ArrayList<Entry> entries = new ArrayList<>();

        entries.add(new Entry(0f, 4f));
        entries.add(new Entry(1f, 1f));
        entries.add(new Entry(2f, 2f));
        entries.add(new Entry(3f, 4f));

        return entries;
    }

//    private void startNewSignalActivity(LoggedInUser user){
//        Intent intent = new Intent(this, NewSignal.class);
//        intent.putExtra("user", user);
//        startActivity(intent);
//    }
}
