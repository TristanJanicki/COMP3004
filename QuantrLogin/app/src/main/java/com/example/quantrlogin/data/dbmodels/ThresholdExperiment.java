package com.example.quantrlogin.data.dbmodels;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThresholdExperiment extends Experiment implements java.io.Serializable{
    private String indicator, ticker, threshold, status, last_updated_at, update_requested_at, directional_bias;
    private String[] event_dates;
    private double[] price_deltas, volumes;
    private double price_delta_mean, price_delta_std_dev, volumes_mean, t_test_p, t_test_t, shapiro_p2, shapiro_w2;

    public ThresholdExperiment(String id, String indicator, String ticker, String threshold, String status, double[] price_deltas, double price_delta_mean, double price_delta_std_dev, String[] event_dates, double t_test_t, double t_test_p, double shapiro_p2, double shapiro_w2, double[] volumes, double volumes_mean, String last_updated_at, String directional_bias){
        super(id);
        this.indicator = indicator;
        this.threshold = threshold;
        this.ticker = ticker;
        this.status = status;
        this.price_deltas = price_deltas;
        this.price_delta_mean = price_delta_mean;
        this.price_delta_std_dev = price_delta_std_dev;
        this.event_dates = event_dates;
        this.t_test_p = t_test_p;
        this.t_test_t = t_test_t;
        this.shapiro_p2=shapiro_p2;
        this.shapiro_w2=shapiro_w2;
        this.price_delta_std_dev=price_delta_mean;
        this.volumes=volumes;
        this.volumes_mean=volumes_mean;
        this.event_dates=event_dates;
        this.directional_bias = directional_bias;
        this.last_updated_at = last_updated_at;
    }

    public double getPrice_delta_mean() {
        return price_delta_mean;
    }

    public double getPrice_delta_std_dev() {
        return price_delta_std_dev;
    }

    public double getT_test_p() {
        return t_test_p;
    }

    public double getShapiro_p2() {
        return shapiro_p2;
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

    public static ThresholdExperiment convertJSONObjectToDbObject(JSONObject obj) throws JSONException{
        JSONArray price_deltas = obj.getJSONArray("price_deltas");
        double[] converted_price_deltas = new double[price_deltas.length()];
        for (int i = 0; i < price_deltas.length(); i ++) {
            converted_price_deltas[i] = (double) price_deltas.get(i);
        }

        JSONArray event_dates = obj.getJSONArray("event_dates");
        String[] converted_event_dates = new String[event_dates.length()];
        for(int i = 0; i < event_dates.length(); i ++){
            converted_event_dates[i] = (String) event_dates.get(i);
        }

        JSONArray volumes = obj.getJSONArray("volumes");
        double[] converted_volumes = new double[volumes.length()];

        for (int i = 0; i < volumes.length(); i ++){
            converted_volumes[i] = (double) volumes.get(i);
        }

        return new ThresholdExperiment(
                obj.getString("experiment_id"),
                obj.getString("indicator"),
                obj.getString("ticker"),
                obj.getString("threshold"),
                obj.getString("status"),
                converted_price_deltas,
                obj.getDouble("price_delta_mean"),
                obj.getDouble("price_delta_std_dev"),
                converted_event_dates,
                obj.getDouble("t_test_t"),
                obj.getDouble("t_test_p"),
                obj.getDouble("shapiro_p"),
                obj.getDouble("shapiro_w"),
                converted_volumes,
                obj.getDouble("volumes_mean"),
                obj.getString("updated_at"),
                obj.getString("directional_bias")
        );
    }
}
