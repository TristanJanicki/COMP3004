package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;

public class MySignals extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_signals);

        final Button button_home = findViewById(R.id.home1);

        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openHomeActivity();
            }
        });

    }

    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeView.class);
        startActivity(intent);
    }
}
