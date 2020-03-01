# run loops that update every permutation of api parameters

# use the dataset files to get the name of every nasdaq and S&P500 listed company
# get all the api parameters from alpha vantage, i think this loop is missing some time intervals
import requests
import csv
import time
import sys
import os
import technicals
import datetime
import time
import logging


def getTickerData(filename, dataset):
    f = open(filename, "r")
    contents = f.readlines()

    for l in range(len(contents)-2):
        tickData = contents[l+1]
        ticker = ''
        for x in range(len(tickData)):
            if(tickData[x] == '|'):
                break
            else:
                ticker += tickData[x]
        dataset.append(ticker)


def getTickersFromDatasets():
    dataset = []
    getTickerData("./datasets/nasdaqlisted.txt", dataset)
    getTickerData("./datasets/otherlisted.txt", dataset)
    return dataset


logging.basicConfig()
logger = logging.getLogger()
log_handler = logging.StreamHandler(sys.stdout)
log_handler.setFormatter(logging.Formatter("%(asctime)s - %(message)s"))
log_handler.setLevel(logging.INFO)
logger.addHandler(log_handler)
logger.setLevel(logging.INFO)

list_of_technicals = "RSI,SMA,EMA,WMA,DEMA,TEMA,TRIMA,KAMA,MAMA,VWAP,T3,MACD,MACDEXT,STOCH,STOCHF,STOCHRSI,WILLR,ADX,ADXR,APO,PPO,MOM,BOP,CCI,CMO,ROC,ROCR,AROON,AROONOSC,MFI,TRIX,ULTOSC,DX,MINUS_DI,PLUS_DI,MINUS_DM,PLUS_DM,BBANDS,MIDPOINT,MIDPRICE,SAR,TRANGE,ATR,NATR,AD,ADOSC,OBV,HT_TRENDLINE,HT_SINE,HT_TRENDMODE,HT_DCPERIOD,HT_DCPHASE,HT_PHASOR".split(
    ",")
tickers = getTickersFromDatasets()
api_call_count = 0

series_types = ["close", "open", "high", "low"]
time_intervals = ["1min", "5min", "15min",
                  "30min", "daily", "weekly", "monthly"]

# TODO: update this list to be continuous in some range
time_periods = ["5", "10", "15", "20", "100", "200"]

time_series_types = ["INTRA_DAY" "DAILY",
                     "DAILY_ADJUSTED", "WEEKLY", "WEEKLY_ADJUSTED", "MONTHLY"]

api_keys = ["3284ADU2OA9K1TMP", "MQB8T0YUNFCKRXY3", "0YFP4YQSAHYZ6ILZ", "BA49YIFTFXOURC9U"]
api_limit_per_minute = 5 * len(api_keys)

# 5 per minute
# 500 per day
for tpt in time_series_types:
    for s in series_types:
        for t in list_of_technicals:
            for i in time_intervals:
                for ticker in tickers:
                    if api_call_count == (500 * len(api_keys)):
                        exit(1)

                    api_key = api_keys[api_call_count % len(api_keys)]
                    if api_call_count > 0 and api_call_count % api_limit_per_minute == 0:
                        time.sleep(60)
                    logger.info(
                        ("about to get price data for", ticker, i, tpt, s))
                    _, exists = technicals.getPriceData(
                        ticker, i, time_series_type=tpt, api_key=api_key, series_type=s)

                    if exists == True:
                        api_call_count += 1

                    for tp in time_periods:
                        if api_call_count == 500 * len(api_keys):
                            exit(1)
                        api_key = api_keys[api_call_count % len(api_keys)]
                        if api_call_count > 0 and api_call_count % api_limit_per_minute == 0:
                            time.sleep(60)
                        logger.info(("about to get technical data for ",
                                     ticker, t, tp, i, s))
                        _, exists = technicals.getTechnicalData(
                            t, ticker, tp, i, s, api_key=api_key)

                        if exists == True:
                            api_call_count += 1
