package com.example.quantrlogin.data.dbmodels;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;
import java.util.logging.Logger;

public class ThresholdExperiment extends Experiment implements Serializable {
    private String indicator, ticker, threshold, directional_bias;
    String[] event_dates;
    double[] price_deltas;
    double price_delta_std_dev;
    double price_delta_mean;
    double price_delta_mode;
    double t_test_p;

    public ThresholdExperiment(String id, String indicator, String ticker, String threshold, String[] event_dates, double[] price_deltas, double price_delta_std_dev, double price_delta_mean, double price_delta_mode, double t_test_p, String direcitonal_bias){
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
        this.directional_bias = direcitonal_bias;
    }

    public String getDirectionalBias() {
        return directional_bias;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getTicker() {
        return ticker;
    }

    public String getThreshold() {
        return threshold;
    }

    public double getPrice_delta_mean() {
        return price_delta_mean;
    }

    public double getPrice_delta_mode() {
        return price_delta_mode;
    }

    public double getPrice_delta_std_dev() {
        return price_delta_std_dev;
    }

    public double getT_test_p() {
        return t_test_p;
    }

    public double[] getPrice_deltas() {
        return price_deltas;
    }

    public String[] getEvent_dates() {
        return event_dates;
    }

    public static ThresholdExperiment convertJSONObjectToDbObject(JSONObject obj) throws JSONException{
        double[] price_deltas;

        String[] deltas = obj.getString("price_deltas").split(",");
        Logger.getGlobal().warning(obj.toString());
        Logger.getGlobal().warning(obj.getString("price_deltas"));
        price_deltas = new double[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            try{
                price_deltas[i] = Float.parseFloat(deltas[i]);
            }catch (Exception e){
                Logger.getGlobal().warning("unable to parse price delta into threshold object");
            }
        }
        Logger.getGlobal().warning("Price Deltas " + Arrays.toString(price_deltas));
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
                obj.getDouble("t_test_p"),
                obj.getString("direction_bias")
        );
    }
}
