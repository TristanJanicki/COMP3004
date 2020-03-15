package com.example.quantrlogin.data.swagger_models;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class ThresholdExperiment {
    private String indicator, ticker, direction_bias;
    private float threshold;

    public ThresholdExperiment(String indicator, String ticker, float threshold, String direction_bias){
        this.indicator = indicator;
        this.ticker = ticker;
        this.threshold = threshold;
        this.direction_bias = direction_bias;
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

    public String getDirectionBias() {
        return direction_bias;
    }

    public static ThresholdExperiment convertDbModelToSwaggerModel(ThresholdExperiment db){
        return new ThresholdExperiment(
                db.getIndicator(),
                db.getTicker(),
                db.getThreshold(),
                db.direction_bias
        );
    }

    @NonNull
    @Override
    public String toString() {
        try{
            JSONObject str = new JSONObject();
            str.put("indicator", indicator);
            str.put("threshold", threshold);
            str.put("ticker", ticker);
            str.put("direction_bias", direction_bias);
            return str.toString();
        }catch (JSONException j){
            j.printStackTrace();
            return "<>";
        }
    }
}
