package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import networking_handlers.SignUpHandler;

public class SignUp extends AppCompatActivity {
    private Button button_SignUp;
    private EditText email, username;
    private RadioGroup rg;
    private RadioButton selectedSubscription, premiumButton, freemiumButton;
    private CheckBox termsOfService;
    private String tag;
    private boolean isPremium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        rg = findViewById(R.id.radioGroup2);
        email = findViewById(R.id.email);
        username = findViewById(R.id.signup_username);
        termsOfService = findViewById(R.id.termsOfService);
        button_SignUp = findViewById(R.id.signUp2);
        email.setText("tt700joe@gmail.com");
        username.setText("Tristan G. J.");

        //send POST Sign up request
        //after that works
        //then proceed to redirect to the authorization page
        button_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSubscription = findViewById(rg.getCheckedRadioButtonId());
                if (selectedSubscription == null){
                    premiumButton = findViewById(R.id.paidSubscription);
                    freemiumButton = findViewById(R.id.freeSubscription);
                    freemiumButton.setError("Required Field");
                    premiumButton.setError("Required Field");
                }
                if (!termsOfService.isChecked()){
                    termsOfService.setError("Required Field");
                    return;
                }
                if (selectedSubscription.getText().toString().toLowerCase().contains("premium")){
                    isPremium = true;
                }else{
                    isPremium = false;
                }
                Logger.getGlobal().warning("Is Premium: " + isPremium);

                final String emailStr = email.getText().toString();
                final String usernameStr = username.getText().toString();
                final String accountTypeStr = (isPremium ? "premium" : "freemium");
                SignUpHandler sh = new SignUpHandler(usernameStr, emailStr, usernameStr, accountTypeStr);
                sh.execute();
                try {
                    Result.GenericNetworkResult signupResult = (Result.GenericNetworkResult) sh.get();
                    Logger.getGlobal().warning(signupResult.toString());
                    switch(signupResult.getCode()){
                        case 201:
                            openSignInActivity();
                            break;
                        case 409:
                            email.setError("This email is taken.");
                            break;
                        case 500:
                            button_SignUp.setError("Oops somethign went wrong");
                            break;
                    }
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //goes to authorization for now, until we have main screen setup
    public void openSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


}
