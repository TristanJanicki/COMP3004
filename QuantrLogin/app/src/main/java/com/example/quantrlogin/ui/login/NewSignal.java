package com.example.quantrlogin.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.swagger_models.ThresholdExperiment;

import networking_handlers.CreateExperimentsHandler;

public class NewSignal extends AppCompatActivity{
    Button createSignalButton;
    SearchView tickerSearch;
    Spinner indicatorChooser, directionChooser;
    RadioButton longStrat, shortTrat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_signal_activity);
        createSignalButton = findViewById(R.id.createSignalButotn);
        tickerSearch = findViewById(R.id.searchStock);
        indicatorChooser = findViewById(R.id.indicatorChooser);
        directionChooser = findViewById(R.id.directionChooser);

        tickerSearch.setQuery("AMD", false);
        String[] indicatorChoices = new String[]{"RSI", "SMA 10", "SMA 20", "SMA 50", "SMA 100", "SMA 200", "TRIX", "EMA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, indicatorChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        indicatorChooser.setAdapter(adapter);


        String[] directionChoices = new String[]{"Crosses Above", "Crosses Below"};
        ArrayAdapter<String> dirAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, directionChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        indicatorChooser.setAdapter(dirAdapter);


        final Bundle b = getIntent().getExtras();

        if (b == null){
            System.out.println("GET INTENT EXTRAS IS NULL");
            return;
        }

        createSignalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ticker = tickerSearch.getQuery().toString();
                String indicator = indicatorChooser.getSelectedItem().toString();
                ThresholdExperiment input = new ThresholdExperiment(indicator, ticker, 101);
//                CorrelationExperiment input = new CorrelationExperiment("GBP", "EUR", 0);
                CreateExperimentsHandler ceh = new CreateExperimentsHandler();
                ceh.execute(b.get("user"), input);
                try{
                    Result r = ceh.get();
                    if (r instanceof Result.Success){
                        System.out.println("Succesfully Created Experiment");
                    }else if (r instanceof Result.Error){
                        System.out.println("Failed To Create Experiment: " + r.toString());
                    }else if (r instanceof Result.NotAllowed){
                        System.out.println("YOU CANT DO THAT!");
                    }else if (r instanceof Result.AlreadyExists){
                        System.out.println("That experiment exists already");
                    }else{
                        System.out.println("Unknown method response");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
