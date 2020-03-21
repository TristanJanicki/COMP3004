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

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.swagger_models.ThresholdExperiment;

import java.util.Arrays;
import java.util.logging.Logger;

import networking_handlers.CreateExperimentsHandler;

public class NewCorrelSignal extends AppCompatActivity{
    Button createSignalButton;
    Button createThreshold;
    EditText asset1,asset2;
    Spinner indicatorChooser;
    LoggedInUser user;
    Logger l = Logger.getGlobal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_correl_signal_activity);
        createSignalButton = findViewById(R.id.createSignalButotn);
        //tickerSearch = findViewById(R.id.searchStock);
       // asset1=findViewById(R.id.asset1);
        //asset2=findViewById(R.id.asset2);
        indicatorChooser = findViewById(R.id.indicatorChooser);


        String[] indicatorChoices = new String[]{"equity_currency", "currency_equity","currency_currency"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, indicatorChoices);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        indicatorChooser.setAdapter(adapter);

        createThreshold=findViewById(R.id.button2);
        createThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewCorrelSignal.this,NewSignal.class);
                startActivity(intent);
            }
        });



     //   Arrays.sort(availableTickers);
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

      /*  createSignalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         //       String ticker = tickerSearch.getText().toString();

               // CorrelationExperiment input = new CorrelationExperiment();
                //CorrelationExperiment input = new CorrelationExperiment("GBP", "EUR", 0);
                CreateExperimentsHandler ceh = new CreateExperimentsHandler();
              //  ceh.execute(user, input);
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
        });*/

    }

    void openActivity(Class c, LoggedInUser user){
        Intent intent = new Intent(this, c);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}
