package networking_handlers;

import android.os.AsyncTask;

import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.CorrelationExperiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;
import com.example.quantrlogin.data.dbmodels.ThresholdExperiment;

import org.json.JSONObject;

import java.util.UUID;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateExperimentsHandler extends AsyncTask<Object, Void, Result> {

    @Override
    protected Result doInBackground(Object... inputs) {

        try{
            if (!(inputs[0] instanceof LoggedInUser)){
                return new Result.Error(new Exception("First argument must be of type LoggedInUser"));
            }
            LoggedInUser user = (LoggedInUser) inputs[0];
            String urlPostfix = "/v1/experiments";

            JSONObject jsonBody = new JSONObject();
            String bodyStr;
            if (inputs[1] instanceof CorrelationExperiment){
                urlPostfix += "/correlation";
                CorrelationExperiment obj = (CorrelationExperiment) inputs[1];
//                bodyStr = "{\"experiment_id\":" + user.getUserId() + "\",\"asset_1\" : \"" + obj.getAsset_1() + "\",\"asset_2\" : \"" + obj.getAsset_2() + "\"}";
                jsonBody.put("experiment_id", obj.getId());
                jsonBody.put("userID", user.getUserId());
                jsonBody.put("asset_1", obj.getAsset_1());
                jsonBody.put("asset_2", obj.getAsset_2());
            }else if(inputs[1] instanceof ThresholdExperiment){
                urlPostfix += "/threshold";
                ThresholdExperiment obj = (ThresholdExperiment) inputs[1];
//                bodyStr = "{\"experiment_id\":" + user.getUserId() + "\",\"indicator\": \"" +  obj.getIndicator() + "\",\"threshold\": " + obj.getThreshold() + ",\"ticker\": \"" + obj.getTicker() + "\"}";
                jsonBody.put("experiment_id", obj.getId());
                jsonBody.put("userID", user.getUserId());
                jsonBody.put("indicator", obj.getIndicator());
                jsonBody.put("ticker", obj.getTicker());
            }else{
                return new Result.Error(new Exception("Second argument must be of type Experiment"));
            }
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, jsonBody.toString());
//            RequestBody body = RequestBody.create(mediaType, bodyStr);
            Request request = new Request.Builder()
                    .url(networking_statics.experimentsService + urlPostfix)
                    .method("POST", body)
                    .addHeader("X-Request-ID", UUID.randomUUID().toString())
                    .addHeader("idToken", user.getIdToken())
                    .addHeader("user-id", user.getUserId())
                    .addHeader("Content-Type", "application/json")
                    .build();
            Call c = client.newCall(request);

            Response r = c.execute();
            System.out.println("CODE: "+ r.code());
            System.out.println("Message: " + r.message());
            switch (r.code()){
                case 200:
                    return new Result.Success(200);
                case 400:
                    return new Result.Error(new Exception("Something went wrong"));
                case 401:
                    return new Result.NotAllowed(r.message());
                case 409:
                    return new Result.AlreadyExists();
                case 500:
                    // TODO: add failed items to a Dead Letter Queue (we try to send these again later)
                    return new Result.Error(new Exception(r.body().toString()));
            }

        }catch (Exception e){
            e.printStackTrace();
            return new Result.Error(e);
        }

        return new Result.Error(new Exception("Oops something went wrong"));
    }
}
