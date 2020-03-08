import requests
import csv
import time
import pandas as pd
import os


def genCSV(ticker, function, apiUrl, skip_existing=True):

    print(apiUrl)
    path = "./stock_data/" + ticker + "_" + function + "Data.csv"
    if skip_existing and os._exists(path):
        return path, True
    # csv
    r = requests.get(apiUrl)
    if r.text.count("API call frequency") == 0 and r.text.count("does not exist") == 0 and r.text.count("Invalid API call.") == 0:
        with open(path, "w") as f:
            print(r.text)
            f.write(r.text)
            f.flush()
            f.close()
            return path, False
    return "", False


def getTechnicalData(technical, ticker, time_period="5", interval='daily', series_type='close', api_key="", return_csv_obj=False, skip_existing=True):
    url = "https://www.alphavantage.co/query?outputsize=full&function="+technical + "&symbol=" + ticker + "&interval=" + \
        interval + "&time_period=" + time_period + "&series_type=" + \
        series_type + "&datatype=csv&apikey=" + api_key
    path, exists = genCSV(ticker, technical + time_period +
                          interval + series_type, url, skip_existing=skip_existing)

    if return_csv_obj == True:
        return csv.reader(open(path)), exists
    return path, exists


def getCurrencyData(fromCurr, toCurr, api_key="", return_csv_obj=False):
    fromCurr = fromCurr.split(",")[1]
    toCurr = toCurr.split(",")[1]
    url = "https://www.alphavantage.co/query?function=FX_DAILY&output_size=full&from_symbol=" + \
        fromCurr + "&to_symbol=" + toCurr + "&apikey=" + api_key + "&datatype=csv"
    path, exists = genCSV(ticker=fromCurr + "_" + toCurr, function="FX", apiUrl=url)
    print("PATH: ", path)
    if return_csv_obj == True:
        return csv.reader(open(path)), exists
    return path, exists


def getPriceData(ticker, interval='daily', time_series_type="DAILY", series_type='close', api_key="", return_csv_obj=False):
    url = "https://www.alphavantage.co/query?function=TIME_SERIES_" + time_series_type + "&outputsize=full&symbol=" + \
        ticker + "&interval=" + interval + "&series_type=" + \
        series_type + "&datatype=csv&apikey=" + api_key
    path, exists = genCSV(ticker, "TIME_SERIES_DAILY", url)

    if return_csv_obj == True:
        return csv.reader(open(path)), exists
    return path, exists


if __name__ == "__main__":
    technicals = ["ADX", "ATR", "AROONOSC", "SAR",
                  "RSI", "ROCR", "MFI", "TRIX", "PPO"]
    # ["NVDA", "AMD", "APH", "APHA", "BKNG", "BIDU", "CGC", "CSIQ", "GRUB", "MSFT", "ROKU", "SHOP", "SPY", "TSLA", "TWLO", "WIX", "ZBRA"]
    tickers = ["AMD"]
    api_call_count = 0

    series_types = ["close"]  # , "open", "high", "low"]
    # theres a huge list of technicals available at https://www.alphavantage.co/documentation/
    technicals = ["RSI"]
    time_periods = ["14"]
    time_intervals = ["5min"]
    time_series_types = ["INTRA_DAY" "DAILY",
                         "DAILY_ADJUSTED", "WEEKLY", "WEEKLY_ADJUSTED", "MONTHLY"]

    for tpt in time_series_types:
        for ticker in tickers:
            for s in series_types:
                for t in technicals:
                    for tp in time_periods:
                        for i in time_intervals:
                            if api_call_count > 0 and api_call_count % 5 == 0:
                                time.sleep(60)
                            getPriceData(
                                ticker, tp, i, timer_series_type=tpt, series_type=s)
                            getTechnicalData(t, ticker, tp, i, s)
                            api_call_count += 1
