package com.example.quantrlogin.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.quantrlogin.R;
import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;

import networking_handlers.DeleteExperimentsHandler;
import networking_handlers.output.AuthChallengeRequiredParameters;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private Button button_SignUp;
    private Button button_Login;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        loginButton.setEnabled(true);

        if (savedInstanceState != null && savedInstanceState.get("email") != null){
            usernameEditText.setText(savedInstanceState.get("email").toString());
        }

        usernameEditText.setText("tristan.janicki@gmail.com");
        passwordEditText.setText("newPassword1");
//        usernameEditText.setText("tt700joe@gmail.com");
//        passwordEditText.setText("newPassword1");

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getAuthChallenge() != null){
                    openAuthChallengeActivity(loginResult.getAuthChallenge());
                }
                if (loginResult.getSuccess() != null) {
                    /*
                    //////////////////////////////////////////////////////////// GET EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////
                    GetExperimentsHandler geh = new GetExperimentsHandler();
                    System.out.println("ABOUT TO EXECUTE GET EXPERIMENTS HANLDER");
                    geh.execute(loginResult.getSuccess());
                    try {
                        Result r = geh.get();
                        if (r instanceof Result.GetExperimentsResult){
                            System.out.println("CORRS" + Arrays.toString(((Result.GetExperimentsResult) r).getCorrelationExperiments()));
                            System.out.println("THRESHS" + Arrays.toString(((Result.GetExperimentsResult) r).getThresholdExperiments()));
                        }else{
                            System.out.println("NOT SUCCESS: "+ r.toString());
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //////////////////////////////////////////////////////////// END GET EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////

                     */

                    /*
                    //////////////////////////////////////////////////////////// CREATE EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////
//                    ThresholdExperiment input = new ThresholdExperiment("TRIX", "AMD", 101);
                    CorrelationExperiment input = new CorrelationExperiment("GBP", "EUR", 0);
                    CreateExperimentsHandler ceh = new CreateExperimentsHandler();
                    ceh.execute(loginResult.getSuccess(), input);
                    try{
                        Result r = ceh.get();
                        if (r instanceof Result.Success){
                            System.out.println("Succesfully Created Experiment");
                        }else if (r instanceof Result.Error){
                            System.out.println("Failed To Create Experiment: " + r.toString());
                        }else if (r instanceof Result.NotAllowed){
                            System.out.println("YOU CANT DO THAT!");
                        }else if (r instanceof Result.AlreadyExists){
                            System.out.println("That experiment exists already");
                        }else{
                            System.out.println("Unknown method response");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //////////////////////////////////////////////////////////// END CREATE EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////

                     */

                    //////////////////////////////////////////////////////////// DELETE EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////

                    Experiment toDelete = new Experiment("5671f3e5-c00b-47be-9096-6e8e963f32d5");

                    DeleteExperimentsHandler deh = new DeleteExperimentsHandler();

                    deh.execute(loginResult.getSuccess(), toDelete);

                    try{
                        Result r = deh.get();
                        if (r instanceof Result.Success){
                            System.out.println("Succesfully Deleted Experiment");
                        }else if (r instanceof Result.Error){
                            System.out.println("Failed To Delete Experiment: " + r.toString());
                        }else if (r instanceof Result.NotAllowed){
                            System.out.println("YOU CANT DO THAT!");
                        }else if (r instanceof Result.AlreadyExists){
                            System.out.println("That experiment exists already");
                        }else{
                            System.out.println("Unknown method response");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    //////////////////////////////////////////////////////////// END DELETE EXPERIMENTS EXAMPLE //////////////////////////////////////////////////////////

                    updateUiWithUser(loginResult.getSuccess());
                }
//                setResult(Activity.RESULT_OK);
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        button_SignUp = findViewById(R.id.signUp);
        button_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });
    }

    public void openSignUpActivity() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
    }
    public void openAuthChallengeActivity(AuthChallengeRequiredParameters params){
        Bundle data = new Bundle();
        data.putString("email", params.email);
        data.putString("sessionId", params.sessionID);
        data.putString("challenge_name", params.challengeName);
        System.out.println("Bundle Before It Goes Into Intent" + data.toString());
        Intent intent = new Intent(this, Authorization.class);
        intent.putExtras(data);
        startActivity(intent);
    }

    private void updateUiWithUser(LoggedInUser model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();

        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
