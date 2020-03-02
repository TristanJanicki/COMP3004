package com.example.quantrlogin.data;

import networking_handlers.output.AuthChallengeRequiredParameters;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private Result() {
    }

    @Override
    public String toString() {
        if (this instanceof Result.Success) {
            Result.Success success = (Result.Success) this;
            return "Success[data=" + success.getData().toString() + "]";
        } else if (this instanceof Result.Error) {
            Result.Error error = (Result.Error) this;
            return "Error[exception=" + error.getError().toString() + "]";
        }
        return "";
    }

    public final static class AuthChallengeRequired extends Result {
        private AuthChallengeRequiredParameters data;

        public AuthChallengeRequired (AuthChallengeRequiredParameters data){
            this.data = data;
        }

        public AuthChallengeRequiredParameters getParameters(){
            return this.data;
        }
    }

    public final static class GenericNetworkResult extends Result {
        private int code;
        private String message;

        public GenericNetworkResult(int code, String message){
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

    public final static class NotAllowed extends Result {
        public NotAllowed (){
        }
    }

    // Success sub-class
    public final static class Success<T> extends Result {
        private T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return this.data;
        }
    }

    // Error sub-class
    public final static class Error extends Result {
        private Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return this.error;
        }
    }
}
