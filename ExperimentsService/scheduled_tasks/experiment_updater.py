# native imports
import logging
import sys
import uuid
import multiprocessing
import time
import datetime
import random
from multiprocessing import Process, Pool, Queue, current_process, freeze_support

# external library imports
from sqlalchemy.sql import exists
import threading
import concurrent.futures
from sqlalchemy.exc import SQLAlchemyError

# internal library imports
from swagger_server.infrastructure.db.mysql import mysql
import swagger_server.experiments as exp
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.StatusEnum import StatusEnum
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment


# initializing loggers and sql manager & getting cpu count
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

def saveThresholdToDataBase(threshold, t_test_t, t_test_p, shapiro_w2, shapiro_p2,  rsi_price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, event_dates, direction_bias):
    experiment_id = str(uuid.uuid4())
    thExp = ThresholdExperiment(experiment_id, "RSI", threshold, t)
    thExp.price_deltas = ','.join(str(v) for v in list(rsi_price_deltas))
    thExp.price_delta_mean = float(price_delta_mean)
    thExp.price_delta_std_dev = float(price_delta_std_dev)
    thExp.event_dates = ",".join(event_dates)    
    thExp.experiment_id = experiment_id
    thExp.direction_bias = direction_bias
    thExp.threshold = threshold
    thExp.ticker = t
    thExp.price_deltas = ','.join(str(v) for v in list(rsi_price_deltas))
    thExp.price_delta_mean = price_delta_mean
    thExp.price_delta_std_dev = price_delta_std_dev
    thExp.event_dates = ','.join(str(v) for v in list(event_dates))
    thExp.t_test_p = t_test_p
    thExp.t_test_t = t_test_t
    thExp.shapiro_p2 = shapiro_p2
    thExp.shapiro_w2 = shapiro_w2
    thExp.price_delta_std_dev = price_delta_mean
    thExp.volumes = ','.join(str(v) for v in list(volumes))
    thExp.volumes_mean = volumes_mean,
    thExp.event_dates = ','.join(str(v) for v in list(event_dates))
    thExp.status = "updated"
    ts = time.time()
    stringTimeStamp = datetime.datetime.fromtimestamp(
        ts).strftime('%Y-%m-%d %H:%M:%S')
    thExp.last_updated_at = stringTimeStamp

    try:
        sqlManager.session.add(thExp)
        sqlManager.session.commit()
    except SQLAlchemyError as e:
        sqlManager.session.rollback()
        raise e

tickers = ["AMD"]
for t in tickers:
    test_results = exp.get_all_rsi_price_distributions(t, direction_bias="bearish", saveToDataBase=True, callback=saveThresholdToDataBase)
    for r in test_results:
        threshold, t_test_t, t_test_p, shapiro_w2, shapiro_p2, rsi_price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, event_dates = r

        
        
        

# with Pool(processes=cpu_count) as pool:
#     for thExp in sqlManager.session.query(ThresholdExperiment).filter_by(status="update_requested"):
#         print("EXP: ", thExp)
#         if thExp.indicator == "RSI":  # have an elif for each indicator
#             try:
#                 print("EXP: ", thExp)
#                 res = pool.apply_async(exp.get_rsi_threshold_move_distribution, args=(
#                     [thExp.ticker], "ALL", thExp.threshold, 1, False))
#                 history, history_std_dev, history_mean, price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, corr_matrix, event_dates = res.get()
#                 thExp.price_deltas = ','.join(str(v)
#                                               for v in list(price_deltas))
#                 thExp.price_delta_mean = float(price_delta_mean)
#                 thExp.price_delta_std_dev = float(price_delta_std_dev)
#                 thExp.event_dates = ",".join(event_dates)
#                 thExp.status = "updated"
#                 ts = time.time()
#                 stringTimeStamp = datetime.datetime.fromtimestamp(
#                     ts).strftime('%Y-%m-%d %H:%M:%S')
#                 thExp.last_updated_at = stringTimeStamp
#                 sqlManager.session.commit()
#             except SQLAlchemyError as e:
#                 sqlManager.session.rollback()

#     for corrExp in sqlManager.session.query(CorrelationExperiment).filter_by(status="update_requested"):
#         print("EXP", corrExp)
#         res = pool.apply_async(exp.getAssetCorrelation, args=(
#             corrExp.asset_1, corrExp.asset_2, corrExp.asset_combo))
#         corrMatrix = res.get()
#         print(corrMatrix)
#         corrExp.correlation = corrMatrix[0][1]
#         sqlManager.session.commit()
