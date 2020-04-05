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

def saveThresholdToDataBase(threshold, t_test_t, t_test_p, shapiro_w2, shapiro_p2,  rsi_price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, event_dates, direction_bias, price_delta_mode, skewness, kurtosis):
    experiment_id = str(uuid.uuid4())
    thExp = ThresholdExperiment(experiment_id, "RSI", threshold, t)
    thExp.experiment_id = experiment_id
    thExp.price_deltas = ','.join(str(v) for v in list(rsi_price_deltas))
    thExp.price_delta_mean = float(price_delta_mean)
    thExp.price_delta_std_dev = float(price_delta_std_dev)
    thExp.event_dates = ",".join(event_dates)    
    thExp.direction_bias = direction_bias
    thExp.threshold = threshold
    thExp.ticker = t
    thExp.price_deltas = ','.join(str(v) for v in list(rsi_price_deltas))
    thExp.price_delta_std_dev = price_delta_std_dev
    thExp.price_delta_mode = price_delta_mode
    thExp.event_dates = ','.join(str(v) for v in list(event_dates))
    thExp.t_test_p = t_test_p
    thExp.t_test_t = t_test_t
    thExp.shapiro_p2 = shapiro_p2
    thExp.shapiro_w2 = shapiro_w2
    thExp.volumes = ','.join(str(v) for v in list(volumes))
    thExp.volumes_mean = volumes_mean,
    thExp.event_dates = ','.join(str(v) for v in list(event_dates))
    thExp.skewness = skewness
    thExp.kurtosis = kurtosis
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
      
        

with Pool(processes=cpu_count) as pool:
    for thExp in sqlManager.session.query(ThresholdExperiment).filter_by(status="update_requested"):
        print("EXP: ", thExp)
        if thExp.indicator == "RSI":  # have an elif for each indicator
            try:
                print("EXP: ", thExp)
                res = pool.apply_async(exp.get_rsi_threshold_move_distribution, args=(
                    [thExp.ticker], "ALL", thExp.threshold, 1, False))
                history, history_std_dev, history_mean, price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, corr_matrix, event_dates = res.get()
                thExp.price_deltas = ','.join(str(v)
                                              for v in list(price_deltas))
                thExp.price_delta_mean = float(price_delta_mean)
                thExp.price_delta_std_dev = float(price_delta_std_dev)
                thExp.event_dates = ",".join(event_dates)
                thExp.status = "updated"
                ts = time.time()
                stringTimeStamp = datetime.datetime.fromtimestamp(
                    ts).strftime('%Y-%m-%d %H:%M:%S')
                thExp.last_updated_at = stringTimeStamp
                sqlManager.session.commit()
            except SQLAlchemyError as e:
                logger.warning(e)
                sqlManager.session.rollback()

    # TODO: make it so that the correlation experiment is done within a time frame not the whole dataset.
    for corrExp in sqlManager.session.query(CorrelationExperiment).filter_by(status="update_requested"):
        print("EXP", corrExp)
        try:
            res = pool.apply_async(exp.getAssetCorrelation, args=(
                corrExp.asset_1, corrExp.asset_2, corrExp.asset_combo))
            results = res.get()
            # initializing variables for readability
            # corrMatrix = results[0]            
            # print("CORR:", corrMatrix)
            corrExp.correlation = float(results[0][0][1])
            corrExp.asset_1_deltas = ','.join(str(v) for v in list(results[1]))
            corrExp.asset_2_deltas = ','.join(str(v) for v in list(results[2]))
            sqlManager.session.commit()
        except Exception as e:
            raise e
            logger.warning(e)
            sqlManager.session.rollback()
