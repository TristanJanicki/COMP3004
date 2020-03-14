package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

public class ThresholdExperiment extends Experiment {
    private String indicator, ticker, threshold;
    String[] event_dates;
    double[] price_deltas;
    double price_delta_std_dev;
    double price_delta_mean;
    double price_delta_mode;
    double t_test_p;

    public ThresholdExperiment(String id, String indicator, String ticker, String threshold, String[] event_dates, double[] price_deltas, double price_delta_std_dev, double price_delta_mean, double price_delta_mode, double t_test_p){
        super(id);
        this.indicator = indicator;
        this.ticker = ticker;
        this.threshold = threshold;
        this.event_dates = event_dates;
        this.price_deltas = price_deltas;
        this.price_delta_std_dev = price_delta_std_dev;
        this.price_delta_mean =price_delta_mean;
        this.price_delta_mode=price_delta_mode;
        this.t_test_p=t_test_p;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getTicker() {
        return ticker;
    }

    public static ThresholdExperiment convertJSONObjectToDbObject(JSONObject obj) throws JSONException{
        double[] price_deltas;

        String[] deltas = obj.getString("price_deltas").split(",");
        price_deltas = new double[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            try{
                price_deltas[i] = Float.parseFloat(deltas[i]);
            }catch (Exception e){
                Logger.getGlobal().warning("unable to parse price delta into threshold object");
            }
        }

        return new ThresholdExperiment(
                obj.getString("experiment_id"),
                obj.getString("indicator"),
                obj.getString("ticker"),
                obj.getString("threshold"),
                obj.getString("event_dates").split(","),
                price_deltas,
                obj.getDouble("price_delta_std_dev"),
                obj.getDouble("price_delta_mean"),
                obj.getDouble("price_delta_mode"),
                obj.getDouble("t_test_p")
        );
    }
}
