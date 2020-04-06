package com.example.quantrlogin.experiments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.swagger_models.ThresholdExperiment;
import com.example.quantrlogin.ui.login.HomeAcitvity;

import java.util.Arrays;
import java.util.logging.Logger;

import networking_handlers.CreateExperimentsHandler;

public class NewSignal extends AppCompatActivity{
    Button createSignalButton;
    Button createCorrelation;
    EditText tickerSearch, thresholdField;
    Spinner indicatorChooser, directionChooser;
    RadioButton longStrat, shortStrat;
    String[] availableTickers = new String[]{"TSLA", "AMD", "NVDA", "SPCE"};
    LoggedInUser user;
    Logger l = Logger.getGlobal();
    private ConstraintLayout newSignalConstraint;
    private boolean checkDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signal_activity);
        newSignalConstraint = findViewById(R.id.createSig);
        createSignalButton = findViewById(R.id.createSignalButotn);
        createCorrelation=findViewById(R.id.button2);
        tickerSearch = findViewById(R.id.searchStock);
        indicatorChooser = findViewById(R.id.indicatorChooser);
        directionChooser = findViewById(R.id.directionChooser);
        thresholdField = findViewById(R.id.setNewSignalThreshold);
        longStrat = findViewById(R.id.longButt);
        shortStrat = findViewById(R.id.shortButt);

        checkDarkMode = HomeAcitvity.getDarkMode();
        updateDarkMode();

        String[] indicatorChoices = new String[]{"RSI", "SMA 10", "SMA 20", "SMA 50", "SMA 100", "SMA 200"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, indicatorChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        indicatorChooser.setAdapter(adapter);


        String[] directionChoices = new String[]{"Crosses Above", "Crosses Below"};
        ArrayAdapter<String> dirAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, directionChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        directionChooser.setAdapter(dirAdapter);


        createCorrelation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewSignal.this,NewCorrelSignal.class);
                startActivity(intent);
            }
        });

        Arrays.sort(availableTickers);
        final Bundle b = getIntent().getExtras();

        if (b == null){
            l.warning("GET INTENT EXTRAS IS NULL");
            return;
        }
        if (!b.containsKey("user")){
            l.warning("NO USER IN CREATE SIGNAL");
            return;
        }
        user = (LoggedInUser) b.get("user");

        createSignalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticker = tickerSearch.getText().toString();
                String indicator = indicatorChooser.getSelectedItem().toString();
                float threshold = Float.parseFloat(thresholdField.getText().toString());
                String directional_bias = (directionChooser.getSelectedItem().toString().contains("Above") ? "crosses_above" : "crosses_below");

                ThresholdExperiment input = new ThresholdExperiment(indicator, ticker, threshold, directional_bias);
//                CorrelationExperiment input = new CorrelationExperiment("GBP", "EUR", 0);
                CreateExperimentsHandler ceh = new CreateExperimentsHandler();
                ceh.execute(user, input);
                try{
                    Result r = ceh.get();
                    if (r instanceof Result.Success){
                        l.warning("Succesfully Created Experiment");
                        openActivity(MySignals.class, user);
                    }else if (r instanceof Result.Error){
                        l.warning("Failed To Create Experiment: " + r.toString());
                    }else if (r instanceof Result.NotAllowed){
                        l.warning("YOU CANT DO THAT!");
                    }else if (r instanceof Result.AlreadyExists){
                        l.warning("That experiment exists already");
                    }else{
                        l.warning("Unknown method response");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    void openActivity(Class c, LoggedInUser user){
        Intent intent = new Intent(this, c);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void updateDarkMode() {
        newSignalConstraint = findViewById(R.id.createSig);


        if (checkDarkMode) { //if in light mode
            //make necessary changes to convert to dark mode
            newSignalConstraint.setBackgroundColor(getResources().getColor(R.color.DarkNavy));
            tickerSearch.setTextColor(getResources().getColor(R.color.LightGrey));
            thresholdField.setTextColor(getResources().getColor(R.color.LightGrey));
            longStrat.setTextColor(getResources().getColor(R.color.LightGrey));
            shortStrat.setTextColor(getResources().getColor(R.color.LightGrey));
            tickerSearch.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            thresholdField.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            longStrat.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            shortStrat.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));

        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            newSignalConstraint.setBackgroundColor(getResources().getColor(R.color.White));
            tickerSearch.setTextColor(getResources().getColor(R.color.Grey));
            thresholdField.setTextColor(getResources().getColor(R.color.Grey));
            longStrat.setTextColor(getResources().getColor(R.color.Grey));
            shortStrat.setTextColor(getResources().getColor(R.color.Grey));
            tickerSearch.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            thresholdField.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            longStrat.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            shortStrat.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
        }
    }

}
