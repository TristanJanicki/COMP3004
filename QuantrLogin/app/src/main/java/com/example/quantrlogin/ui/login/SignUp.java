package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.quantrlogin.R;
import com.example.quantrlogin.ui.login.LoginViewModel;
import com.example.quantrlogin.ui.login.LoginViewModelFactory;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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
    private EditText email, username;
    private CheckBox termsOfService;
    //private OkHttpClient client = new OkHttpClient();
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button_SignUp = findViewById(R.id.signUp2);
        email = findViewById(R.id.email);
        username = findViewById(R.id.signup_username);
        termsOfService = findViewById(R.id.termsOfService);
        final String emailStr = email.getText().toString();
        final String usernameStr = username.getText().toString();

        //send POST Sign up request
        //after that works
        //then proceed to redirect to the authorization page
        button_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!termsOfService.isChecked()) return; // TODO: make a tool-tip popup when a user clicks signup but doesn't have TOS checked.
                SignUpHandler sh = new SignUpHandler(usernameStr, emailStr, "", "");
                sh.execute();
                try {
                    Object signupResult = sh.get();
                    System.out.println(signupResult.toString());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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



<<<<<<< HEAD
<<<<<<< HEAD
                final OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n    \"email\": \"farzalk@gmail.com\",\n    \"firstName\": \"Farzal\",\n    \"lastName\": \"Khan\"\n}");
                final Request request = new Request.Builder()
                        .url("ec2-54-165-183-238.compute-1.amazonaws.com:8080/v1/users/signup")
                        .method("POST", body)
                        .addHeader("X-Request-ID", "43ed32ce-6e3f-4918-afbb-b1412161c811")
                        .addHeader("Content-Type", "application/json")
                        .build();
                //Response response = client.newCall(request).execute();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            //Log.i(tag, response.body().string());
                            response = client.newCall(request).execute();

                            Log.i(tag, response.body().string());

                            openAuthorizationActivity();
                        }
                    }
                });



//                OkHttpClient client = new OkHttpClient().newBuilder()
//                        .build();
//                MediaType mediaType = MediaType.parse("application/json");
//                RequestBody body = RequestBody.create(mediaType, "{\n \" email \": \"farzalk@gmail.com\",    \"firstName\": \"Farzal\",    \"lastName\": \"Khan\"}");
//                Request request = new Request.Builder()
//                        .url(url)
//                        .method("POST", body)
//                        .addHeader("X-Request-ID", "{{$guid}}") //5d09ec8a-6ef2-43ce-8b80-25339cd8d5c4
//                        .addHeader("Content-Type", "application/json")
//                        .build();

//                client.newCall(request).enqueue(new Callback() {
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
=======
>>>>>>> 8c6bd97091b74c64b7ebeb7a1ffb243af0c5885d
=======
>>>>>>> 8c6bd97091b74c64b7ebeb7a1ffb243af0c5885d
            }
        });
    }

    //goes to authorization for now, until we have main screen setup
    public void openAuthorizationActivity() {
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
    }


}
