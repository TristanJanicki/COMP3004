# run loops that update every permutation of api parameters

# use the dataset files to get the name of every nasdaq and S&P500 listed company
# get all the api parameters from alpha vantage, i think this loop is missing some time intervals
import requests
import csv
import time
import sys
sys.path.append('COMP3004/DataSerivce/')
from technicals import getPriceData,getTechnicalData

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
	getTickerData("nasdaqlisted.txt",dataset)
	getTickerData("otherlisted.txt",dataset)
	return dataset

technicals = "SMA,EMA,WMA,DEMA,TEMA,TRIMA,KAMA,MAMA,VWAP,T3,MACD,MACDEXT,STOCH,STOCHF,RSI,STOCHRSI,WILLR,ADX,ADXR,APO,PPO,MOM,BOP,CCI,CMO,ROC,ROCR,AROON,AROONOSC,MFI,TRIX,ULTOSC,DX,MINUS_DI,PLUS_DI,MINUS_DM,PLUS_DM,BBANDS,MIDPOINT,MIDPRICE,SAR,TRANGE,ATR,NATR,AD,ADOSC,OBV,HT_TRENDLINE,HT_SINE,HT_TRENDMODE,HT_DCPERIOD,HT_DCPHASE,HT_PHASOR".split(",")
tickers = getTickersFromDatasets()
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
