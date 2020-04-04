package com.example.quantrlogin.experiments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.swagger_models.CorrelationExperiment;
import com.example.quantrlogin.ui.login.LoginActivity;

import java.util.logging.Logger;

import networking_handlers.CreateExperimentsHandler;



public class NewCorrelSignal extends AppCompatActivity{
    Button createSignalButton;
    Button createThreshold;
    EditText asset1,asset2,correlation,correlValue;
    LoggedInUser user;
    Logger l = Logger.getGlobal();

    private ConstraintLayout newCorrelConstraint;
    private boolean checkDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_correl_signal_activity);
        createSignalButton = findViewById(R.id.createSignalButotn);
        //tickerSearch = findViewById(R.id.searchStock);
        asset1=findViewById(R.id.searchAsset);
        asset2=findViewById(R.id.searchAsset2);
        correlation = findViewById(R.id.correlation);
        correlValue = findViewById(R.id.correl);

        checkDarkMode = LoginActivity.getDarkMode();
        updateDarkMode();


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

        createSignalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float correl = Float.valueOf(correlation.getText().toString());
                String ass1=asset1.getText().toString();
                String ass2= asset2.getText().toString();
                CorrelationExperiment input = new CorrelationExperiment(ass1,ass2,correl);
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
        });

    }

    void openActivity(Class c, LoggedInUser user){
        Intent intent = new Intent(this, c);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void updateDarkMode() {
        newCorrelConstraint = findViewById(R.id.createCorrelSig);

        if (checkDarkMode) { //if in light mode
            //make necessary changes to convert to dark mode
            newCorrelConstraint.setBackgroundColor(getResources().getColor(R.color.DarkNavy));
            asset1.setTextColor(getResources().getColor(R.color.LightGrey));
            asset2.setTextColor(getResources().getColor(R.color.LightGrey));
            correlation.setTextColor(getResources().getColor(R.color.LightGrey));
            correlValue.setTextColor(getResources().getColor(R.color.LightGrey));
            asset1.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            asset2.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            correlation.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));
            correlValue.setBackgroundTintList(getResources().getColorStateList(R.color.LightGrey));

        } else { //else in dark mode
            //make necessary changes to convert to dark mode
            newCorrelConstraint.setBackgroundColor(getResources().getColor(R.color.White));
            asset1.setTextColor(getResources().getColor(R.color.Grey));
            asset2.setTextColor(getResources().getColor(R.color.Grey));
            correlation.setTextColor(getResources().getColor(R.color.Grey));
            correlValue.setTextColor(getResources().getColor(R.color.Grey));
            asset1.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            asset2.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            correlation.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
            correlValue.setBackgroundTintList(getResources().getColorStateList(R.color.Grey));
        }
    }

}
