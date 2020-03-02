package com.example.quantrlogin.ui.login;

import androidx.annotation.Nullable;

import com.example.quantrlogin.data.model.LoggedInUser;

import networking_handlers.output.AuthChallengeRequiredParameters;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUser user;
    @Nullable
    private Integer error;
    @Nullable
    private AuthChallengeRequiredParameters authChallengeParams;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUser user) {
        this.user = user;
    }

    LoginResult(@Nullable AuthChallengeRequiredParameters params){
        this.authChallengeParams = params;
    }

    @Nullable
    LoggedInUser getSuccess() {
        return user;
    }

    @Nullable AuthChallengeRequiredParameters getAuthChallenge() {return this.authChallengeParams;}

    @Nullable
    Integer getError() {
        return error;
    }
}
