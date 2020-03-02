package com.example.quantrlogin.data.swagger_models;

public class ThresholdExperiment {
    private String indicator, ticker;

    public ThresholdExperiment(String indicator, String ticker){
        this.indicator = indicator;
        this.ticker = ticker;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getTicker() {
        return ticker;
    }

    public static com.example.quantrlogin.data.swagger_models.ThresholdExperiment convertDbModelToSwaggerModel(ThresholdExperiment db){
        return new com.example.quantrlogin.data.swagger_models.ThresholdExperiment(
                db.getIndicator(),
                db.getTicker()
        );
    }
}
