package com.example.quantrlogin.data;

import android.os.AsyncTask;

import java.io.IOException;

import networking_handlers.SignInHandler;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result login(String username, String password) {

        try {

            SignInHandler signInHandler = new SignInHandler(username, password);
            AsyncTask result = signInHandler.execute();
            Result res = (Result) result.get();

            return res;
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
