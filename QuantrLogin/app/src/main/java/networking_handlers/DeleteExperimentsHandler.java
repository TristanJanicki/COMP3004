package networking_handlers;

import android.os.AsyncTask;

import com.example.quantrlogin.data.Result;
import com.example.quantrlogin.data.dbmodels.Experiment;
import com.example.quantrlogin.data.dbmodels.LoggedInUser;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeleteExperimentsHandler extends AsyncTask<Object, Void, Result> {
    @Override
    protected Result doInBackground(Object... inputs) {

        if (!(inputs[0] instanceof LoggedInUser)){
            return new Result.Error(new Exception("First argument must be a LoggedInUser"));
        }
        if(!(inputs[1] instanceof Experiment)){
            return new Result.Error(new Exception("Second argument must be an Experiment"));
        }

        LoggedInUser user = (LoggedInUser) inputs[0];
        Experiment experiment = (Experiment) inputs[1];

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(networking_statics.url + "/v1/users/experiments")
                .method("DELETE", null)
                .addHeader("X-Request-ID", UUID.randomUUID().toString())
                .addHeader("idToken", user.getIdToken())
                .addHeader("user-id", user.getUserId())
                .addHeader("experiment_id", experiment.getId())
                .build();
        Call c = client.newCall(request);

        try{
            Response r = c.execute();

            switch(r.code()){
                case 200:
                    return new Result.Success<>(200);
                case 400:
                    return new Result.Error(new Exception(r.message()));
                case 401:
                    return new Result.NotAllowed(r.message());
                case 409:
                    return new Result.AlreadyExists();
                case 500:
                    return new Result.Error(new Exception(r.message()));
            }

        }catch (IOException e){
            return new Result.Error(e);
        }

        return new Result.Error(new Exception("Something went wrong..."));
    }
}
