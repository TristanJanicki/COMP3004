import requests
import csv
import time
import pandas as pd
import os

def genCSV(ticker, function, apiUrl):

    path = "./stock_data/" + ticker + "_" + function + "Data.csv"

    # csv
    r = requests.get(apiUrl)
    if r.text.count("API call frequency") == 0:
        with open(path, "a") as f:
            f.write(r.text)
            f.flush()
            f.close()
            return path
    return ""

def getTechnicalData(technical, ticker, time_period="5", interval='daily', series_type='close', return_csv_obj=False):
    url = "https://www.alphavantage.co/query?outputsize=full&function="+technical +"&symbol=" + ticker + "&interval=" + interval +"&time_period=" + time_period + "&series_type=" + series_type + "&datatype=csv&apikey=MQB8T0YUNFCKRXY3"
    path = genCSV(ticker, technical + time_period + interval + series_type, url)
    if return_csv_obj == True:
        return csv.reader(open(path))

def getPriceData(ticker, time_period="5", interval='daily', timer_series_type="DAILY", series_type='close', return_csv_obj=False):
    url = "https://www.alphavantage.co/query?function=TIME_SERIES_"+ timer_series_type + "&outputsize=full&symbol=" + ticker + "&interval=" + interval +"&time_period=" + time_period + "&series_type=" + series_type + "&datatype=csv&apikey=MQB8T0YUNFCKRXY3"
    path = genCSV(ticker, "TIME_SERIES_DAILY", url)
    if return_csv_obj == True:
        return csv.reader(open(path))

if __name__ == "__main__":
    technicals = ["ADX", "ATR", "AROONOSC", "SAR", "RSI", "ROCR", "MFI", "TRIX", "PPO"]
    tickers = ["AMD"]#["NVDA", "AMD", "APH", "APHA", "BKNG", "BIDU", "CGC", "CSIQ", "GRUB", "MSFT", "ROKU", "SHOP", "SPY", "TSLA", "TWLO", "WIX", "ZBRA"]
    api_call_count = 0

    series_types = ["close"]#, "open", "high", "low"]
    technicals = ["RSI"] # theres a huge list of technicals available at https://www.alphavantage.co/documentation/
    time_periods = ["14"]
    time_intervals = ["5min"]
    time_series_types = ["INTRA_DAY" "DAILY", "DAILY_ADJUSTED", "WEEKLY", "WEEKLY_ADJUSTED", "MONTHLY"]

    for tpt in time_series_types:
        for ticker in tickers:
            for s in series_types:
                for t in technicals:
                    for tp in time_periods:
                        for i in time_intervals:
                            if api_call_count > 0 and api_call_count % 5 == 0:
                                time.sleep(60)
                            getPriceData(ticker, tp, i,timer_series_type=tpt, series_type=s)
                            getTechnicalData(t, ticker, tp, i, s)
                            api_call_count += 1
