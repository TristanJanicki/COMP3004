package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import java.util.concurrent.ExecutionException;

import networking_handlers.GetExperimentsHandler;

public class MySignals extends AppCompatActivity {
    LoggedInUser user;
    Experiment[] usersExperiments;
    Button button_home, newSignalButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_signals);

        final Bundle b = getIntent().getExtras();

        if (b == null){
            System.out.println("GET INTENT EXTRAS IS NULL");
            return;
        }
        if (!b.containsKey("user")){
            System.out.println("LOADING MY SIGNALS WITHOUT PASSING IN A USER");
            return;
        }
        user = (LoggedInUser) b.get("user");
        usersExperiments = getSignals(user);
        button_home = findViewById(R.id.home1);

        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity(user);
            }
        });

        newSignalButton = findViewById(R.id.addNewSignal);
        newSignalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(NewSignal.class, user);
            }
        });

    }

    void openActivity(Class c, LoggedInUser user){
        Intent intent = new Intent(this, c);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void openHomeActivity(LoggedInUser user) {
        Intent intent = new Intent(this, HomeAcitvity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private Experiment[] getSignals(LoggedInUser user){
        GetExperimentsHandler geh = new GetExperimentsHandler();
        System.out.println("ABOUT TO EXECUTE GET EXPERIMENTS HANLDER");
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
                setUiExperiments(allExperiments);
                return allExperiments;
            }else{
                System.out.println("NOT SUCCESS: "+ r.toString());
                return new Experiment[]{};
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return new Experiment[]{};
    }

    private void setUiExperiments(Experiment[] experiments){
        // TODO: convert this to use an expandable list view not hardcoding a set amount signals

        Button sig1, sig2, sig3;

        sig1 = findViewById(R.id.signal1);
        sig2 = findViewById(R.id.signal2);
        sig3 = findViewById(R.id.signal3);

        if (experiments.length >= 1)applySignalButtonStyling(sig1, experiments[experiments.length - 1]);
        if (experiments.length >= 2) applySignalButtonStyling(sig2, experiments[1]);
        if (experiments.length >= 3) applySignalButtonStyling(sig3, experiments[2]);
    }

    private void applySignalButtonStyling(Button b, Experiment e){
        if (e instanceof CorrelationExperiment){
            CorrelationExperiment c = (CorrelationExperiment) e;
            b.setText(c.getAsset_1() + ":" + c.getAsset_2() + ":" + c.getCorrelation());
        }else if(e instanceof ThresholdExperiment){
            ThresholdExperiment t = (ThresholdExperiment) e;
            b.setText(t.getTicker() + ":" + t.getIndicator() + ":" + t.getThreshold());
        }
    }
}
