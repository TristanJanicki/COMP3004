package com.example.quantrlogin.experiments;

import android.content.Intent;
import android.graphics.Color;
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
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import networking_handlers.GetExperimentsHandler;

public class MySignals extends Fragment {
    private LoggedInUser user;
    private Button editSignal;
    private ImageButton newSignal;
    private int counter = 1;
    private LinearLayout linearLayout = null;
    private Logger l = Logger.getGlobal();

    public MySignals() {
        //Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        user = (LoggedInUser) getActivity().getIntent().getSerializableExtra("user");

        getSignals(user);


        View view = inflater.inflate(R.layout.activity_my_signals, container, false);
        linearLayout = view.findViewById(R.id.linearLayout);
        l.warning("EXPERIMENTS LENGTH " + user.experiments.length);
        l.warning(Arrays.toString(user.experiments));
        for (Experiment e : user.experiments){
            addExperimentButton(e);
        }

          /*detailedThresholdView = view.findViewById(R.id.DetailedThresholdView);
          activ.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {

              }
          });*/

        editSignal = view.findViewById(R.id.addNewSignal);
        editSignal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewSignal.class);
                intent.putExtras(getActivity().getIntent().getExtras());
                startActivity(intent);
            }
        });

        return view;
    }

    private Experiment[] getSignals(LoggedInUser user){
        GetExperimentsHandler geh = new GetExperimentsHandler();
        Logger.getGlobal().warning("ABOUT TO EXECUTE GET EXPERIMENTS HANLDER");
        geh.execute(user);
        try {
            Result r = geh.get();
            if (r instanceof Result.GetExperimentsResult){
                CorrelationExperiment[] corrs = ((Result.GetExperimentsResult) r).getCorrelationExperiments();
                ThresholdExperiment[] threshs = ((Result.GetExperimentsResult) r).getThresholdExperiments();
                Experiment[] allExperiments = new Experiment[corrs.length + threshs.length];

                int i = 0;
                for(CorrelationExperiment c : corrs){
                    allExperiments[i] = c;
                    ++i;
                }
                for(ThresholdExperiment t : threshs){
                    allExperiments[i] = t;
                    ++i;
                }
                user.experiments = allExperiments;
                return allExperiments;
            }else{
                Logger.getGlobal().warning("NOT SUCCESS: "+ r.toString());
                return new Experiment[]{};
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Experiment[]{};
    }



    public void addExperimentButton(Experiment e){
        if (counter < 6){
            Logger.getGlobal().warning("ADDING EXPERIMENT BUTTON");
            Button newButton = new Button(getContext());
            newButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (e instanceof ThresholdExperiment){
                ThresholdExperiment t = (ThresholdExperiment) e;
                newButton.setText(t.getTicker() + " " + t.getIndicator() + " " + t.getDirectionalBias() + " " + t.getThreshold());
            }else if(e instanceof CorrelationExperiment){
                CorrelationExperiment c = (CorrelationExperiment) e;
                newButton.setText(c.getAsset_1() + " Correlation With " + c.getAsset_2());
            }
            newButton.setId(counter);
            newButton.setBackgroundColor(Color.parseColor("#80B0B0"));
            linearLayout.addView(newButton);
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent detailedSignalView = new Intent(this, DetailedSignalView.class);
                }
            });
            counter++;
        } else {
            Toast.makeText(getActivity(), "You cannot create more than 5 signals.", Toast.LENGTH_SHORT).show();
        }
    }
}
