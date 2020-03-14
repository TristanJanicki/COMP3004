package com.example.quantrlogin.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;

public class MySignals extends Fragment {
    private LoggedInUser user;
    private Experiment[] usersExperiments;
    private Button editSignal;
    private ImageButton newSignal;
    private int counter = 1;
    private LinearLayout linearLayout = null;

    public MySignals() {
        //Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_signals, container, false);

        linearLayout = view.findViewById(R.id.linearLayout);

        newSignal = view.findViewById(R.id.addSignal);
        newSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addButton();
            }
        });

//        editSignal = view.findViewById(R.id.addNewSignal);
//        editSignal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), NewSignal.class);
//                startActivity(intent);
//            }
//        });

        return view;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_my_signals);
//
//        final Bundle b = getIntent().getExtras();
//
//        if (b == null){
//            System.out.println("GET INTENT EXTRAS IS NULL");
//            return;
//        }
//        if (!b.containsKey("user")){
//            System.out.println("LOADING MY SIGNALS WITHOUT PASSING IN A USER");
//            return;
//        }
//        user = (LoggedInUser) b.get("user");
//        usersExperiments = getSignals(user);
//
//    }
//
//    void openActivity(Class c, LoggedInUser user){
//        Intent intent = new Intent(this, c);
//        intent.putExtra("user", user);
//        startActivity(intent);
//    }
//
//    public void openHomeActivity(LoggedInUser user) {
//        Intent intent = new Intent(this, HomeAcitvity.class);
//        intent.putExtra("user", user);
//        startActivity(intent);
//    }
//
//    private Experiment[] getSignals(LoggedInUser user){
//        GetExperimentsHandler geh = new GetExperimentsHandler();
//        System.out.println("ABOUT TO EXECUTE GET EXPERIMENTS HANLDER");
//        geh.execute(user);
//        try {
//            Result r = geh.get();
//            if (r instanceof Result.GetExperimentsResult){
//                CorrelationExperiment[] corrs = ((Result.GetExperimentsResult) r).getCorrelationExperiments();
//                ThresholdExperiment[] threshs = ((Result.GetExperimentsResult) r).getThresholdExperiments();
//                Experiment[] allExperiments = new Experiment[corrs.length + threshs.length];
//
//                int i = 0;
//                for(CorrelationExperiment c : corrs){
//                    allExperiments[i] = c;
//                    ++i;
//                }
//                for(ThresholdExperiment t : threshs){
//                    allExperiments[i] = t;
//                    ++i;
//                }
//                setUiExperiments(allExperiments);
//                return allExperiments;
//            }else{
//                System.out.println("NOT SUCCESS: "+ r.toString());
//                return new Experiment[]{};
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }
//        return new Experiment[]{};
//    }
//
//    private void setUiExperiments(Experiment[] experiments){
//        // TODO: convert this to use an expandable list view not hardcoding a set amount signals
//
////        Button sig1, sig2, sig3;
////
////        sig1 = findViewById(R.id.signal1);
////        sig2 = findViewById(R.id.signal2);
////        sig3 = findViewById(R.id.signal3);
////
////        if (experiments.length >= 1)applySignalButtonStyling(sig1, experiments[0]);
////        if (experiments.length >= 2) applySignalButtonStyling(sig2, experiments[1]);
////        if (experiments.length >= 3) applySignalButtonStyling(sig3, experiments[2]);
//    }
//
//    private void applySignalButtonStyling(Button b, Experiment e){
//        if (e instanceof CorrelationExperiment){
//            CorrelationExperiment c = (CorrelationExperiment) e;
//            b.setText(c.getAsset_1() + ":" + c.getAsset_2() + ":" + c.getCorrelation());
//        }else if(e instanceof ThresholdExperiment){
//            ThresholdExperiment t = (ThresholdExperiment) e;
//            b.setText(t.getTicker() + ":" + t.getIndicator());
//        }
//    }

    public void addButton(){
        if (counter < 6){
            Button newButton = new Button(getContext());
            newButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            newButton.setText("Signal " + counter);
            newButton.setId(counter);
            linearLayout.addView(newButton);
            counter++;
        } else {
            Toast.makeText(getActivity(), "You cannot create more than 5 signals.", Toast.LENGTH_SHORT).show();
        }
    }
}
