package networking_handlers;

import android.os.AsyncTask;

import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.logging.Logger;

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
            reqBody.put("sessionId", params[0].sessionID);
            reqBody.put("newPassword", params[0].newPassword);

            RequestBody body = RequestBody.create(reqBody.toString(), mediaType);
            Request request = new Request.Builder()
                    .url(networking_statics.userAccounts + "/v1/users/authchallenge")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Request-ID", UUID.randomUUID().toString())
                    .build();
            Call c = client.newCall(request);//

            Logger.getGlobal().warning("About to get response");
            Response r = c.execute();

            JSONObject responseBody = new JSONObject(r.body().string());

            if (r.code() != 201){
                Logger.getGlobal().warning("NOT 201 for AUTH CHALLENGE");
                Logger.getGlobal().warning(responseBody.toString());
            }

            String[] tokenParts = responseBody.getString("idToken").split("\\.");

            Logger.getGlobal().warning(Arrays.toString(tokenParts));
            String idTokenStr = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);// TODO: ask Tristan what this does
            JSONObject cognitoProfile = new JSONObject(idTokenStr);
            Logger.getGlobal().warning(cognitoProfile.toString());
            String userID = cognitoProfile.getString("sub");
            String refreshToken = responseBody.getString("refreshToken");
            String accessToken = responseBody.getString("accessToken");
            return new Result.Success<LoggedInUser> (new LoggedInUser(userID, cognitoProfile.getString("name"), accessToken, responseBody.getString("idToken"), refreshToken, idTokenStr));
        } catch (IOException e) {
            e.printStackTrace();
            return new Result.Error(e);
        }catch(Exception e){
            e.printStackTrace();
            return new Result.Error(e);
        }
    }
}
