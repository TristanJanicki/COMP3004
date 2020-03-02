package networking_handlers;

import android.os.AsyncTask;

import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.model.LoggedInUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import networking_handlers.output.AuthChallengeRequiredParameters;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInHandler extends AsyncTask<Void, Void, Result> {
    String username;
    String password;

    public SignInHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    protected Result doInBackground(Void... voids) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();

            MediaType mediaType = MediaType.parse("application/json");
            JSONObject json = new JSONObject();
            json.put("email", username);
            json.put("password", password);
            System.out.println(json.toString());
            RequestBody body = RequestBody.create(json.toString(), mediaType);
            Request request = new Request.Builder()
                    .url(networking_statics.url + "/v1/users/signin")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Request-ID", UUID.randomUUID().toString())
                    .build();
            Call c = client.newCall(request);//

            try {
                Response r = c.execute();
                JSONObject responseBody = new JSONObject(r.body().string());
                if (r.code() == 307){
                    AuthChallengeRequiredParameters params = new AuthChallengeRequiredParameters(
                            username,
                            responseBody.getString("sessionId"),
                            responseBody.getString("challengeName")
                    );
                    return new Result.AuthChallengeRequired(params);
                }else if (r.code() == 401){
                    return new Result.Error(new Exception("not allowed"));
                }else if (r.code() == 404){
                    return new Result.Error(new Exception("not found"));
                }else if (r.code() == 500){
                    return new Result.Error(new Exception("server error"));
                }
                System.out.println(responseBody.getString("idToken"));
                String[] tokenParts = responseBody.getString("idToken").split("\\.");

                System.out.println(Arrays.toString(tokenParts));
                String idTokenStr = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);// TODO: ask Tristan what this does
                JSONObject cognitoProfile = new JSONObject(idTokenStr);
                System.out.println(cognitoProfile.toString());
                String userID = cognitoProfile.getString("sub");
                String refreshToken = responseBody.getString("refreshToken");
                String accessToken = responseBody.getString("accessToken");


                System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                return new Result.Success<LoggedInUser> (new LoggedInUser(userID, cognitoProfile.getString("first_name"), accessToken, idTokenStr, refreshToken, cognitoProfile));
            } catch (IOException e) {
                System.out.println("XXXX");
                e.printStackTrace();
                return new Result.Error(e);
            }catch (JSONException e){
                System.out.println("YYYYY");
                System.out.println("Failed to parse JSON output");
                e.printStackTrace();
                return new Result.Error(e);
            }catch (Exception e){
                e.printStackTrace();
                return new Result.Error(e);
            }
        }catch (JSONException j){
            System.out.println("ZZZZZZZZZZZ");
            j.printStackTrace();
            return new Result.Error(j);
        }

    }

}