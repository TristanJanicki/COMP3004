package networking_handlers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.quantrlogin.data.Result;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpHandler extends AsyncTask<Void, Void, Result> {

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
    protected Result doInBackground(Void...voids) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject reqBody = new JSONObject();
            reqBody.put("email", email);
            reqBody.put("firstName", firstName);
            reqBody.put("lastName", lastName);

            RequestBody body = RequestBody.create(reqBody.toString(), mediaType);
            Request request = new Request.Builder()
                .url(networking_statics.userAccounts + "/v1/users/signup")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Request-ID", UUID.randomUUID().toString())
                .build();
            Call c = client.newCall(request);//


            Response r = c.execute();
            Log.i("HTTP Response Body", r.body().string());
            Log.i("HTTP Response", r.toString());
            return new Result.GenericNetworkResult(r.code(), r.message());
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }catch(Exception e){
            e.printStackTrace();
            return new Result.Error(e);
        }
    }
}
