package com.example.quantrlogin.data.swagger_models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class CorrelationExperiment {
    private String asset_1, asset_2;
    double correlation;

    public CorrelationExperiment(String asset_1, String asset_2, double correlation){
        this.asset_1 = asset_1;
        this.asset_2 = asset_2;
        this.correlation = correlation;
    }

    public double getCorrelation() {
        return correlation;
    }

    public String getAsset_1() {
        return asset_1;
    }

    public String getAsset_2() {
        return asset_2;
    }

    public static CorrelationExperiment convertDbModelToSwaggerModel(com.example.quantrlogin.data.dbmodels.CorrelationExperiment db){
        return new CorrelationExperiment(
          db.getAsset_1(),
          db.getAsset_2(),
          db.getCorrelation());
    }

    @NonNull
    @Override
    public String toString() {
        try {
            JSONObject str = new JSONObject();
            str.put("asset_1", asset_1);
            str.put("asset_2", asset_2);
            str.put("correlation", correlation);
            return str.toString();
        }catch (JSONException j){
            j.printStackTrace();
            return "<>";
        }
    }
}
