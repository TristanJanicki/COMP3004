package com.example.quantrlogin.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.model.LoggedInUser;

import networking_handlers.CompleteAuthChallengeHandler;
import networking_handlers.output.AuthChallengeRequiredParameters;

public class Authorization extends AppCompatActivity {

    Button confirm;
    EditText newPassword;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        final Bundle b = getIntent().getExtras();

        if (b == null){
            System.out.println("GET INTENT EXTRAS IS NULL");
            return;
        }

        confirm = findViewById(R.id.submitAuthChallenge);
        newPassword = findViewById(R.id.authChallengePassword);
        newPassword.setText("newPassword1");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPwStr = newPassword.getText().toString();

                AuthChallengeRequiredParameters params = new AuthChallengeRequiredParameters(
                        b.getString("email"),
                        b.getString("sessionId"),
                        b.getString("challenge_name"),
                        newPwStr);


                CompleteAuthChallengeHandler cah = new CompleteAuthChallengeHandler();

                cah.execute(params);

                try {
                    Result result = cah.get();

                    if (result instanceof Result.Error){
                        confirm.setError("Something went wrong, try logging in again");
                        return;
                    }else if (result instanceof Result.Success){
                        System.out.println(result.toString());
                        LoggedInUser user = (LoggedInUser) ((Result.Success) result).getData();
                        showHomeScreen(user);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    private void showHomeScreen(LoggedInUser user){
        System.out.println("Showing Home Screen for: " + user.toString());
//        Intent intent = new Intent(this, HomeScreen.class);
//        Bundle b = new Bundle();
    }
}
