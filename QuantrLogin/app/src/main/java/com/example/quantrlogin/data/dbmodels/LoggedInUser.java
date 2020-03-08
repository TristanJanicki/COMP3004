package com.example.quantrlogin.data.dbmodels;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser implements java.io.Serializable{

    private String userId;
    private String displayName;
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Experiment[] experiments;

    public LoggedInUser(String userId, String displayName, String accessToken, String idToken, String refreshToken, Experiment[] experiments) {
        this.userId = userId;
        this.displayName = displayName;
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.experiments = experiments;
    }

    public void setExperiments(Experiment[] experiments) {
        this.experiments = experiments;
    }

    public Experiment[] getExperiments() {
        return experiments;
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
