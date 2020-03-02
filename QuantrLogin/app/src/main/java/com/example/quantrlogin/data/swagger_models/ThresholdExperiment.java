package com.example.quantrlogin.data.swagger_models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ThresholdExperiment {
    private String indicator, ticker;
    private float threshold;

    public ThresholdExperiment(String indicator, String ticker, float threshold){
        this.indicator = indicator;
        this.ticker = ticker;
        this.threshold = threshold;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getTicker() {
        return ticker;
    }

    public float getThreshold() {
        return threshold;
    }

    public static com.example.quantrlogin.data.swagger_models.ThresholdExperiment convertDbModelToSwaggerModel(ThresholdExperiment db){
        return new com.example.quantrlogin.data.swagger_models.ThresholdExperiment(
                db.getIndicator(),
                db.getTicker(),
                db.getThreshold()
        );
    }

    @NonNull
    @Override
    public String toString() {
        try{
            JSONObject str = new JSONObject();
            str.put("indicator", indicator);
            str.put("ticker", ticker);
            str.put("threshold", threshold);
            return str.toString();
        }catch (JSONException j){
            j.printStackTrace();
            return "<>";
        }
    }
}
