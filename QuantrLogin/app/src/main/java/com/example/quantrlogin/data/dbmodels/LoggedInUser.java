package com.example.quantrlogin.data.dbmodels;

import org.json.JSONObject;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private JSONObject cognitoProfile;

    public LoggedInUser(String userId, String displayName, String accessToken, String idToken, String refreshToken, JSONObject cognitoProfile) {
        this.userId = userId;
        this.displayName = displayName;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.cognitoProfile = cognitoProfile;
    }

    public JSONObject getCognitoProfile() {
        return cognitoProfile;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
