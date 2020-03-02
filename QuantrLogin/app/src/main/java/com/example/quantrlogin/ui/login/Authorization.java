package com.example.quantrlogin.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;

import networking_handlers.CompleteAuthChallengeHandler;
import networking_handlers.output.AuthChallengeRequiredParameters;

public class Authorization extends AppCompatActivity {

    Button confirm;
    EditText newPassword;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        if (savedInstanceState == null){
            System.out.println("Bundle in Authorization is NULL WHEN IT SHOULD NOT BE");
            return;
        }

        confirm = findViewById(R.id.submitAuthChallenge);
        newPassword = findViewById(R.id.authChallengePassword);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPwStr = newPassword.getText().toString();

                AuthChallengeRequiredParameters params = new AuthChallengeRequiredParameters(
                        savedInstanceState.getString("email"),
                        savedInstanceState.getString("sessionId"),
                        savedInstanceState.getString("challenge_name"),
                        newPwStr);

                CompleteAuthChallengeHandler cah = new CompleteAuthChallengeHandler();

                cah.execute(params);

                try {
                    Result.GenericNetworkResult result = (Result.GenericNetworkResult) cah.get();
                    System.out.println(result.toString());

                    switch (result.getCode()){
                        case 201:
                            showHomeScreen();
                            break;
                        default:
                            confirm.setError("Something went wrong, try logging in again");
                            break;
                    }
                }catch (Exception e){

                }

            }
        });

    }

    private void showHomeScreen(){
//        Intent intent = new Intent(this, HomeScreen.class);
    }
}
