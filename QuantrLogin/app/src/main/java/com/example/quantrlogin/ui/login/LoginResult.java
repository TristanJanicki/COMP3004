package com.example.quantrlogin.ui.login;

import androidx.annotation.Nullable;

import networking_handlers.output.AuthChallengeRequiredParameters;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedInUserView success;
    @Nullable
    private Integer error;
    @Nullable
    private AuthChallengeRequiredParameters authChallengeParams;

    LoginResult(@Nullable Integer error) {
        this.error = error;
    }

    LoginResult(@Nullable LoggedInUserView success) {
        this.success = success;
    }

    LoginResult(@Nullable AuthChallengeRequiredParameters params){
        this.authChallengeParams = params;
    }

    @Nullable
    LoggedInUserView getSuccess() {
        return success;
    }

    @Nullable AuthChallengeRequiredParameters getAuthChallenge() {return this.authChallengeParams;}

    @Nullable
    Integer getError() {
        return error;
    }
}
