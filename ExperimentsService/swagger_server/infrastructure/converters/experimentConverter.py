
from swagger_server.models.existing_correlation_experiment import ExistingCorrelationExperiment
from swagger_server.models.existing_threshold_experiment import ExistingThresholdExperiment
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment


def convertDbThresholdExperimentToSwaggerExperiment(dbModel:ThresholdExperiment):
    convertedPriceDeltas = []

    for ele in dbModel.price_deltas.split(","):
        convertedPriceDeltas.append(float(ele))

    convertedVolumes = []

    for ele in dbModel.volumes.split(","):
        convertedVolumes.append(float(ele))

    event_dates = dbModel.event_dates.split(",")

    return ExistingThresholdExperiment(dbModel.experiment_id, dbModel.indicator, dbModel.ticker, dbModel.threshold, dbModel.directional_bias, dbModel.status, convertedPriceDeltas, dbModel.price_delta_std_dev, dbModel.price_delta_mean, event_dates, dbModel.t_test_p, dbModel.t_test_t, dbModel.shapiro_p2, dbModel.shapiro_w2, convertedVolumes, dbModel.volumes_mean, dbModel.last_updated_at)

def convertDbCorrelationExperimentToSwaggerExperiment(dbModel:CorrelationExperiment):
    return ExistingCorrelationExperiment(dbModel.experiment_id, dbModel.asset_1, dbModel.asset_2, dbModel.correlation)