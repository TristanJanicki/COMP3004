package networking_handlers;

import android.os.AsyncTask;
import android.util.Log;

import com.example.quantrlogin.data.Result;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import networking_handlers.output.AuthChallengeRequiredParameters;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CompleteAuthChallengeHandler extends AsyncTask<AuthChallengeRequiredParameters, Void, Result> {

    @Override
    protected Result doInBackground(AuthChallengeRequiredParameters... params) {

        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject reqBody = new JSONObject();
            reqBody.put("email", params[0].email);
            reqBody.put("firstName", params[0].sessionID);
            reqBody.put("lastName", params[0].newPassword);

            RequestBody body = RequestBody.create(reqBody.toString(), mediaType);
            Request request = new Request.Builder()
                    .url(networking_statics.url + "/v1/users/authchallenge")
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
