# run loops that update every permutation of api parameters

# use the dataset files to get the name of every nasdaq and S&P500 listed company

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