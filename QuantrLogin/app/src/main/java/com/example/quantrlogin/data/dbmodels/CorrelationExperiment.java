package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

public class CorrelationExperiment extends Experiment {
    private String asset_1, asset_2;
    float correlation;

    public CorrelationExperiment(String id, String asset_1, String asset_2, float correlation){
        super(id);
        this.asset_1 = asset_1;
        this.asset_2 = asset_2;
        this.correlation = correlation;
    }

    public float getCorrelation() {
        return correlation;
    }

    public String getAsset_1() {
        return asset_1;
    }

    public String getAsset_2() {
        return asset_2;
    }

    public static CorrelationExperiment convertJSONObjectToDbObject(JSONObject obj) throws JSONException{
        return new CorrelationExperiment(
            obj.getString("experiment_id"),
            obj.getString("asset_1"),
            obj.getString("asset_2"),
            (float) obj.getDouble("correlation")
        );
    }
}
