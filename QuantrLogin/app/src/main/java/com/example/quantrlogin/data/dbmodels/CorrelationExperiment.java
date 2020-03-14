package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class CorrelationExperiment extends Experiment {
    private String asset_1, asset_2;
    float correlation;
    float[] asset_1_deltas, asset_2_deltas;


    public CorrelationExperiment(String id, String asset_1, String asset_2, float correlation, float[] asset_1_deltas, float[] asset_2_deltas){
        super(id);
        this.asset_1 = asset_1;
        this.asset_2_deltas = asset_2_deltas;
        this.asset_2 = asset_2;
        this.asset_1_deltas = asset_1_deltas;
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
        String[] a1 = obj.getString("asset_1_deltas").split(",");
        float[] asset_1_deltas = new float[a1.length];

        int i = 0;
        for(String x : a1){
            try{
                asset_1_deltas[i++] = Float.parseFloat(x);
            }catch(Exception e){
                Logger.getGlobal().warning("Unable to parse asset 1 price deltas");
            }
        }

        String[] a2 = obj.getString("asset_2_deltas").split(",");

        float[] asset_2_deltas = new float[a2.length];

        i = 0;
        for(String x : a2){
            try{
                asset_2_deltas[i++] = Float.parseFloat(x);
            }catch (Exception e){
                Logger.getGlobal().warning("Unable to parse asset 2 price deltas");
            }
        }

        return new CorrelationExperiment(
            obj.getString("experiment_id"),
            obj.getString("asset_1"),
            obj.getString("asset_2"),
            (float) obj.getDouble("correlation"),
             asset_1_deltas,
             asset_2_deltas
        );
    }
}
