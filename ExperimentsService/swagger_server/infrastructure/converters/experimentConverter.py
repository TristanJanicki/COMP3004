
from swagger_server.models.existing_correlation_experiment import ExistingCorrelationExperiment
from swagger_server.models.existing_threshold_experiment import ExistingThresholdExperiment
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment


def convertDbThresholdExperimentToSwaggerExperiment(dbModel:CorrelationExperiment):
    return ExistingThresholdExperiment(dbModel[0], dbModel[1], dbModel[2], dbModel[3])

def convertDbCorrelationExperimentToSwaggerExperiment(dbModel:ThresholdExperiment):
    return ExistingCorrelationExperiment(dbModel[0], dbModel[1], dbModel[2], dbModel[3], dbModel[4])