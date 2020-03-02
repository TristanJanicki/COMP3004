package com.example.newsignal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity {
    private TextView text;
        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_new_signal);
            Spinner spinner = findViewById(R.id.indicator);
            Spinner spinner2 = findViewById(R.id.threshold);
            ArrayList<String> indicatorList = new ArrayList<>();
            ArrayList<String> threshold = new ArrayList<>();
            indicatorList.add("none selected");
            indicatorList.add("RSI");
            threshold.add("Crosses above");
            threshold.add("Crosses below");
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, indicatorList);
            ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, threshold);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(arrayAdapter);
            spinner2.setAdapter(arrayAdapter2);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String indicatorName;
                    indicatorName = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected: " + indicatorName, Toast.LENGTH_LONG).show();
                }
                @Override
                public void onNothingSelected(AdapterView <?> parent) {
                }
            });
        }
}
