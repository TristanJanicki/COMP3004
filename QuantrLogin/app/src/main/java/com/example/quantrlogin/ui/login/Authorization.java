package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;

import org.json.JSONException;

import java.util.logging.Logger;

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
            Logger.getGlobal().warning("GET INTENT EXTRAS IS NULL");
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
                        Logger.getGlobal().warning(result.toString());
                        LoggedInUser user = (LoggedInUser) ((Result.Success) result).getData();
                        showHomeScreen(user);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

    }

    public void showHomeScreen(LoggedInUser user) {
        try {
            Logger.getGlobal().warning("showing homescreen for = " + user.getProfileAttribute("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, Navigation.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
