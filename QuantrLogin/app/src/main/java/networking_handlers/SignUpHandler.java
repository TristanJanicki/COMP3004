package networking_handlers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpHandler extends AsyncTask {

    String username;
    String email;
    String firstName;
    String lastName;
    JSONObject cognitoProfile;


    public SignUpHandler(String username, String email, String firstName, String lastName){
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create("{\n \"email\": \"" + email + "\",    \"firstName\": \"" + firstName + "\",    \"lastName\": \"" + lastName + "\"}", mediaType);
        Request request = new Request.Builder()
                .url(networking_statics.url + "/v1/users/signup")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Request-ID", UUID.randomUUID().toString())
                .build();
        Call c = client.newCall(request);//

        try {
            Response r = c.execute();
            Log.i("HTTP Response Body", r.body().string());
            Log.i("HTTP Response", r.toString());
            return r;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
