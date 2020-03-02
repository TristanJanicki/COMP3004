package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

public class ThresholdExperiment extends Experiment {
    private String indicator, ticker;

    public ThresholdExperiment(String id, String indicator, String ticker){
        super(id);
        this.indicator = indicator;
        this.ticker = ticker;
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
                obj.getString("ticker")
        );
    }
}
