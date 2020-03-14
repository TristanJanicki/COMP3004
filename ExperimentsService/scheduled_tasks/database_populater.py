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

# popoulate with threshold experiments for these tickers
# thTickers = ["AMD"]
# for t in thTickers:
#     test_results = exp.get_all_rsi_price_distributions(t, direction_bias="bearish", saveToDataBase=True, callback=saveThresholdToDataBase)
        
corrTickers = [("ASX", "SPY"), ("USD:JPY", "USD:CAD")]

for t in corrTickers:
    if ":" not in t[0] and ":" not in t[1]:
        asset_combo = "equity_equity"
    elif ":" in t[0] and ":" in t[1]:
        asset_combo = "currency_currency"
    elif ":" in t[0] and ":" not in t[1]:
        asset_combo = "currecy_equity"
    else:
        asset_combo = "equity_currency"
    print(exp.getAssetCorrelation(t[0], t[1], asset_combo))