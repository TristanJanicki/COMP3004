
from swagger_server.models.existing_correlation_experiment import ExistingCorrelationExperiment
from swagger_server.models.existing_threshold_experiment import ExistingThresholdExperiment
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment


def convertDbThresholdExperimentToSwaggerExperiment(dbModel: ThresholdExperiment):
    # experiment_id, indicator, threshold, ticker, status="up_to_date", price_delta_mode=0, update_requested_at=None,  last_updated_at=None, t_test_t=0, t_test_p=0, shapiro_w2=0, shapiro_p2=0, history="", history_std_dev=0, history_mean=0, price_deltas=0, price_delta_std_dev=0, price_delta_mean=0, volumes=0, volumes_mean=0, corr_matrix=0, event_dates="", directional_bias="bearish", skewness=0, kurtosis=0
    return ExistingThresholdExperiment(dbModel.experiment_id,
                                       dbModel.indicator,
                                       dbModel.ticker,
                                       dbModel.threshold,
                                       dbModel.event_dates,
                                       dbModel.price_deltas,
                                       dbModel.price_delta_std_dev,
                                       dbModel.price_delta_mean,
                                       dbModel.price_delta_mode,
                                       dbModel.t_test_p,
                                       dbModel.directional_bias
                                       )


def convertDbCorrelationExperimentToSwaggerExperiment(dbModel: CorrelationExperiment):
    return ExistingCorrelationExperiment(dbModel.experiment_id, dbModel.asset_1, dbModel.asset_2, dbModel.correlation, dbModel.asset_1_deltas, dbModel.asset_2_deltas)
