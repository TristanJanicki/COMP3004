package com.example.quantr.ui.login;
import com.example.quantr.R;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LoggedInUserView extends AppCompatActivity {
    String name;
    public LoggedInUserView(String n){
        this.name=n;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in_user_view);
    }
}
