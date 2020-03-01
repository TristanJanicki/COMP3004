package networking_handlers;

import android.os.AsyncTask;
import android.util.JsonToken;
import android.util.Log;

import com.example.quantrlogin.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInHandler extends AsyncTask<Void, Void, LoggedInUser> {
    String username;
    String password;
    private String url = "http://ec2-54-210-130-190.compute-1.amazonaws.com:80/v1/users/signup";

    public SignInHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }
    @Override
    protected void onPostExecute(LoggedInUser loggedInUser) {
        System.out.println("on post execute");
        super.onPostExecute(loggedInUser);
    }
    @Override
    protected LoggedInUser doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\n \"email\": \"" + username + "\",password: \"" + password + "\"}", mediaType);
        Request request = new Request.Builder()
                .url(networking_statics.url + "/v1/users/sigin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Request-ID", UUID.randomUUID().toString())
                .build();
        Call c = client.newCall(request);//

        try {
            Response r = c.execute();
            Log.i("HTTP Response Body", r.body().string());
            Log.i("HTTP Response", r.toString());

            if (r.code() != 200){
                return null; // TODO: figure out a better flow for this, error display or something
            }
            JSONObject responseObj = new JSONObject(r.body().string());
            String idTokenStr = new String(Base64.getDecoder().decode(responseObj.getString("idToken")), StandardCharsets.UTF_8).split(".")[1];// TODO: ask Tristan what this does
            JSONObject cognitoProfile = new JSONObject(idTokenStr);
            String userID = cognitoProfile.getString("sub");
            String refreshToken = responseObj.getString("refreshToken");
            String accessToken = responseObj.getString("accessToken");



            return new LoggedInUser(userID, cognitoProfile.getString("first_name"), accessToken, idTokenStr, refreshToken, cognitoProfile);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}