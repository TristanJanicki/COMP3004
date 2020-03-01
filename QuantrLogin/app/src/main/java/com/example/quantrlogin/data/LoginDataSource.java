package com.example.quantrlogin.data;

import android.os.AsyncTask;

import com.example.quantrlogin.data.model.LoggedInUser;
import networking_handlers.SignInHandler;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {

            SignInHandler signInHandler = new SignInHandler(username, password);
            AsyncTask result = signInHandler.execute();

            System.out.println(result.get());
            // TODO: handle loggedInUser authentication

            return new Result.Success<>(null);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
