
from swagger_server.models.existing_experiment_correlation import ExistingExperimentCorrelation
from swagger_server.models.existing_experiment_threshold import ExistingExperimentThreshold
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment


def convertDbThresholdExperimentToSwaggerExperiment(dbModel:CorrelationExperiment):
    return ExistingExperimentThreshold(dbModel[0], dbModel[1], dbModel[2], dbModel[3])

def convertDbCorrelationExperimentToSwaggerExperiment(dbModel:ThresholdExperiment):
    return ExistingExperimentCorrelation(dbModel[0], dbModel[1], dbModel[2], dbModel[3], dbModel[4])