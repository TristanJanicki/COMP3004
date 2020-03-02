# native imports
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment
import logging
import sys
import multiprocessing
import time
import random
from multiprocessing import Process, Pool, Queue, current_process, freeze_support

# external library imports
from sqlalchemy.sql import exists
import threading
import concurrent.futures

# internal library imports
from swagger_server.infrastructure.db.mysql import mysql
import swagger_server.experiments as exp

# initializing loggers and sql manager
cpu_count = multiprocessing.cpu_count()
sqlManager = mysql.SqlManager()
logging.basicConfig()
logger = logging.getLogger()
log_handler = logging.StreamHandler(sys.stdout)
log_handler.setFormatter(logging.Formatter(
    "%(asctime)s - %(message)s - %(funcName)s"))
log_handler.setLevel(logging.INFO)
logger.addHandler(log_handler)
logger.setLevel(logging.INFO)
logger.setLevel(logging.INFO)

thresholds = []
correlations = []

for thExp in sqlManager.session.query(ThresholdExperiment):
    thresholds.append(([thExp.ticker], "ALL", thExp.threshold, 1, False))

for corrExp in sqlManager.session.query(CorrelationExperiment):
    correlations.append(corrExp.asset_1, corrExp.asset_2)

experiment_results = []
records = []
futures = []
q = Queue()

pool = Pool(processes=cpu_count)
res1 = pool.apply_async(exp.getAssetCorrelation, correlations)
res2 = pool.apply_async(exp.get_rsi_threshold_move_distribution, thresholds)
print(res1.get())
print("END")