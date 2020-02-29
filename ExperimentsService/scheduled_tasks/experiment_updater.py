# native imports
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment
import logging

# external library imports
from sqlalchemy.sql import exists
import threading
import concurrent.futures

# internal library imports
from swagger_server.infrastructure.db.mysql import mysql
import swagger_server.experiments as exp

# initializing loggers and sql manager
sqlManager = mysql.SqlManager()
logger = logging.getLogger()
logger.setLevel(logging.INFO)

thresholds = []
correlations = []

stmt = exists().where(ThresholdExperiment.experiment_id == ex)
for thExp in sqlManager.session.query(ThresholdExperiment).filter(ThresholdExperiment.experiment_id == ex):
    thresholds.append("threshold", thExp)

stmt = exists().where(CorrelationExperiment.experiment_id == ex)
for corrExp in sqlManager.session.query(CorrelationExperiment).filter(CorrelationExperiment.experiment_id == ex):
    distributeExecution("correlation", corrExp)



experiment_results = []
records = []
futures = []

def distributeExecution(experiment_type, experiment):
    with concurrent.futures.ThreadPoolExecutor() as executor:
        if experiment_type == "correlation":
            future = executor.submit(exp.getAssetCorrelation, experiment.asset_1, experiment.asset_2)
        elif experiment_type == "threshold":
            if experiment.indicator == "rsi":
                future = executor.submit(exp.get_rsi_threshold_move_distribution, experiment.ticker, "ALL",  1, 1)
            if experiment.indicator == "optimal_rsi":
                future = executor.submit(exp.get_optimal_rsi_days_from_inversion, experiment.ticker, year=experiment.year, rsi_threshold=70, verbose=False)

        futures.append(future)
        experiment_results.append(future.result())