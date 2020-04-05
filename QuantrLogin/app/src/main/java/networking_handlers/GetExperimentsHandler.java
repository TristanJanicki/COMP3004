package networking_handlers;

import android.os.AsyncTask;

import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static networking_handlers.networking_statics.MAXIMUM_RETRIES;

public class GetExperimentsHandler extends AsyncTask<LoggedInUser, Void, Result> {

    private Result doWork(LoggedInUser user, int tries){
        OkHttpClient.Builder builder =  new OkHttpClient().newBuilder();
        builder.readTimeout(5, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
        Request request = new Request.Builder()
                .url(networking_statics.experiments + "/v1/users/experiments")
                .method("GET", null)
                .addHeader("X-Request-ID", UUID.randomUUID().toString())
                .addHeader("idToken", user.getIdToken())
                .addHeader("user-id", user.getUserId())
                .build();
        Call c = client.newCall(request);

        try (Response r = c.execute()) {

            Logger.getGlobal().warning("R.CODE: " + r.code());
            if (r.code() == 200) {
                Logger.getGlobal().warning(r.headers().toString());
                JSONObject responseBody = new JSONObject(r.body().string());
                JSONArray correlations = responseBody.getJSONArray("correlations");
                JSONArray thresholds = responseBody.getJSONArray("thresholds");

                CorrelationExperiment[] dbCorrelations = new CorrelationExperiment[correlations.length()];
                ThresholdExperiment[] dbThresholds = new ThresholdExperiment[thresholds.length()];

                for(int i = 0; i < correlations.length(); i ++){
                    try {
                        dbCorrelations[i] = CorrelationExperiment.convertJSONObjectToDbObject(correlations.getJSONObject(i));
                    }catch(JSONException j){
                        Logger.getGlobal().warning("unable to parse CorrelationExperiment # " + i);
                        j.printStackTrace();
                    }
                }

                for(int i = 0; i < thresholds.length(); i ++){
                    Logger.getGlobal().warning("parsing threshold exp");
                    try{
                        Logger.getGlobal().warning("try");
                        dbThresholds[i] = ThresholdExperiment.convertJSONObjectToDbObject(thresholds.getJSONObject(i));
                    }catch (JSONException j){
                        Logger.getGlobal().warning("unable to parse ThresholdExperiment # " + i);
                        j.printStackTrace();
                    }
                }

                r.close();
                return new Result.GetExperimentsResult(dbThresholds, dbCorrelations);
            } else if (r.code() == 401) {
                Logger.getGlobal().warning("NOT ALLOWED, MOST LIKELY A INVALID TOKEN");
                r.close();
                return new Result.NotAllowed(r.message());
            } else if (r.code() == 404){
                Logger.getGlobal().warning("NOT FOUND");
                r.close();
                return new Result.GenericNetworkResult(404, r.body().toString());
            }else if(r.code() == 409){
                Logger.getGlobal().warning("CONFLICT?");
                r.close();
                return new Result.GenericNetworkResult(r.code(), r.message());
            } else if (r.code() == 500) {
                r.close();
                return new Result.Error(new Exception(r.body().toString()));
            }
        }catch (Exception e){
            e.printStackTrace();
            if (tries < MAXIMUM_RETRIES){
                return doWork(user, tries + 1);
            }
            return new Result.Error(e);
        }
        return new Result.Success("LAST RETURN STATEMENT");
    }

    @Override
    protected Result doInBackground(LoggedInUser... users) {
        return doWork(users[0], 0);
    }
}
