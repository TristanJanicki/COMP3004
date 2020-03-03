package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

public class ThresholdExperiment extends Experiment {
    private String indicator, ticker, threshold;

    public ThresholdExperiment(String id, String indicator, String ticker, String threshold){
        super(id);
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

    public static ThresholdExperiment convertJSONObjectToDbObject(JSONObject obj) throws JSONException{
        return new ThresholdExperiment(
                obj.getString("experiment_id"),
                obj.getString("indicator"),
                obj.getString("ticker"),
                obj.getString("threshold")
        );
    }
}
