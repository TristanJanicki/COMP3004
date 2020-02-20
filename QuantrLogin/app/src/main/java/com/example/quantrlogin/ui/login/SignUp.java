package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.quantrlogin.R;
import com.example.quantrlogin.ui.login.LoginViewModel;
import com.example.quantrlogin.ui.login.LoginViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

import kotlin.Unit;
import networking_handlers.SignUpHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {
    private Button button_SignUp;
    private EditText email;
    //private OkHttpClient client = new OkHttpClient();
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button_SignUp = findViewById(R.id.signUp2);
        email = findViewById(R.id.email);
        final String emailStr = email.getText().toString();

        //send POST Sign up request
        //after that works
        //then proceed to redirect to the authorization page
        button_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                SignUpHandler sh = new SignUpHandler(emailStr, emailStr, emailStr, emailStr);
                SignUpHandler sh = new SignUpHandler("tristan", "tristan.janicki@gmail.com", "Tristan", "Janicki");
                sh.execute();
//                // .enqueue(new Callback() {
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        if (response.isSuccessful()) {
//                            //Log.i(tag, response.body().string());
//
//                            Log.i(tag, response.body().string());
//
//                            openAuthorizationActivity();
//                        }
//                    }
//                });



            }
        });
    }

    //goes to authorization for now, until we have main screen setup
    public void openAuthorizationActivity() {
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
    }


}
