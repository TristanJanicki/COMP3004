package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONException;

import java.util.ArrayList;

import networking_handlers.networking_statics;

public class HomeAcitvity extends Fragment {

    private LoggedInUser user;
    private LineChart lineChart;
    private ConstraintLayout homeLayout;
    private Switch dark_mode;
    private boolean checkDarkMode;
    private TextView username;
    private TextView email;
    private boolean loggedIn = false;
    public static boolean profileDarkModeOn = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);
        user = (LoggedInUser) getActivity().getIntent().getSerializableExtra("user");

        homeLayout = view.findViewById(R.id.home_relativeLayout);
        username = view.findViewById(R.id.profile_name);
        email = view.findViewById(R.id.profile_email);
        dark_mode = view.findViewById(R.id.profile_dark_mode);

        //set username text of user
        email.setText(user.getDisplayName());

        //set email text of user
        try {
            username.setText(user.getProfileAttribute("name"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setDarkMode();

//        lineChart = view.findViewById(R.id.home_chart_view);
//        lineChart.setTouchEnabled(true);
//        lineChart.setPinchZoom(true);

//        LineDataSet homeSet = new LineDataSet(getData(), "Signal 1");
//        homeSet.setColor(ContextCompat.getColor(view.getContext(), R.color.GoldYellow));
//        homeSet.setValueTextColor(ContextCompat.getColor(view.getContext(), R.color.White));
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//
//        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};
//
//        ValueFormatter formatter = new ValueFormatter() {
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                return months[(int) value];
//    }
//        };

//        xAxis.setGranularity(1f);
//        xAxis.setTextColor(ContextCompat.getColor(view.getContext(), R.color.White));
//        xAxis.setValueFormatter(formatter);
//
//        YAxis yAxisRight = lineChart.getAxisRight();
//        yAxisRight.setEnabled(false);
//
//        YAxis yAxis = lineChart.getAxisLeft();
//        yAxis.setTextColor(ContextCompat.getColor(view.getContext(), R.color.White));
//        yAxis.setGranularity(1f);
//
//        LineData data = new LineData(homeSet);
//        lineChart.setData(data);
//        lineChart.invalidate();



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

        dark_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDarkMode();
            }
        });


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

    public void redirectTD(View view){
        Intent tdAmer=new Intent(Intent.ACTION_VIEW, Uri.parse(networking_statics.tdaURL));
        startActivity(tdAmer);
    }

    public static Boolean getDarkMode(){
        return profileDarkModeOn;
    }

    public void setDarkMode(){
        if (dark_mode.isChecked()) { //if in light mode
            //make necessary changes to convert to dark mode
            profileDarkModeOn = true;
            homeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.profile_background_dark));
            username.setTextColor(getResources().getColor(R.color.LightGrey));
            email.setTextColor(getResources().getColor(R.color.LightGrey));

        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            profileDarkModeOn = false;
            homeLayout.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.profile_background_light));
            username.setTextColor(getResources().getColor(R.color.Grey));
            email.setTextColor(getResources().getColor(R.color.Grey));
        }
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