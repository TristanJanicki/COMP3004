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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.quantrlogin.R;

import networking_handlers.networking_statics;

public class HomeAcitvity extends Fragment {

    private Button newSignalB;
    private Button mySignal;
    private Button newSignal;
    private Button signalCalender;

    //private DrawerLayout drawer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);

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
//            System.out.println("GET INTENT EXTRAS IS NULL");
//            return;
//        }
//        if (!b.containsKey("user")){
//            System.out.println("Trying to start home activity without passing in a user!");
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

//    private void startNewSignalActivity(LoggedInUser user){
//        Intent intent = new Intent(this, NewSignal.class);
//        intent.putExtra("user", user);
//        startActivity(intent);
//    }
}
