# run loops that update every permutation of api parameters

# use the dataset files to get the name of every nasdaq and S&P500 listed company
# get all the api parameters from alpha vantage, i think this loop is missing some time intervals
import requests
import csv
import time
import sys
import os
import technicals

def getTickerData(filename,dataset):
	f=open(filename,"r")
	contents=f.readlines()

	for l in range (len(contents)-2):
		tickData=contents[l+1]
		ticker=''
		for x in range (len(tickData)):
			if(tickData[x]=='|'):
				break
			else: 
				ticker+=tickData[x]
		dataset.append(ticker)

def getTickersFromDatasets():
	dataset=[]
	getTickerData("./datasets/nasdaqlisted.txt",dataset)
	getTickerData("./datasets/otherlisted.txt",dataset)
	return dataset

list_of_technicals = "SMA,EMA,WMA,DEMA,TEMA,TRIMA,KAMA,MAMA,VWAP,T3,MACD,MACDEXT,STOCH,STOCHF,RSI,STOCHRSI,WILLR,ADX,ADXR,APO,PPO,MOM,BOP,CCI,CMO,ROC,ROCR,AROON,AROONOSC,MFI,TRIX,ULTOSC,DX,MINUS_DI,PLUS_DI,MINUS_DM,PLUS_DM,BBANDS,MIDPOINT,MIDPRICE,SAR,TRANGE,ATR,NATR,AD,ADOSC,OBV,HT_TRENDLINE,HT_SINE,HT_TRENDMODE,HT_DCPERIOD,HT_DCPHASE,HT_PHASOR".split(",")
tickers = getTickersFromDatasets()
api_call_count = 0

series_types = ["close", "open", "high", "low"]
time_intervals = ["1min", "5min", "15min", "30min", "daily", "weekly", "monthly"]

time_periods = ["5", "10", "15", "20", "100", "200"] # TODO: update this list to be continuous in some range

time_series_types = ["INTRA_DAY" "DAILY", "DAILY_ADJUSTED", "WEEKLY", "WEEKLY_ADJUSTED", "MONTHLY"]
# 5 per minute
# 500 per day
for tpt in time_series_types:
   for ticker in tickers:
        for s in series_types:
            for t in list_of_technicals:
                for tp in time_periods:
                    for i in time_intervals:
                        if api_call_count > 0 and api_call_count % 5 == 0:
                            time.sleep(60)
                        technicals.getPriceData(ticker, tp, i,timer_series_type=tpt, series_type=s)
                        technicals.getTechnicalData(t, ticker, tp, i, s)
                        api_call_count += 1
