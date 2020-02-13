package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.quantrlogin.R;
import com.example.quantrlogin.ui.login.LoginViewModel;
import com.example.quantrlogin.ui.login.LoginViewModelFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {
    private Button button_Login;
    private OkHttpClient client = new OkHttpClient();

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

//    String post(String url, String json) throws IOException {
//        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        button_Login = findViewById(R.id.signUp2);

        //send POST Sign up request
        //after that works
        //then proceed to redirect to the authorization page
        button_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\n    "email": "tristan.janicki@gmail.com",\n    "firstName": "Tristan",\n    "lastName": "Janicki"\n}");
                Request request = new Request.Builder()
                        .url("ec2-18-212-63-242.compute-1.amazonaws.com:80/v1/users/signup")
                        .method("POST", body)
                        .addHeader("X-Request-ID", "5d09ec8a-6ef2-43ce-8b80-25339cd8d5c4")
                        .addHeader("Content-Type", "application/json")
                        .build();
                Response response = client.newCall(request).execute();

                openAuthorizationActivity();
            }
        });
    }

    //goes to authorization for now, until we have main screen setup
    public void openAuthorizationActivity() {
        Intent intent = new Intent(this, Authorization.class);
        startActivity(intent);
    }


}
