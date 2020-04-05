# Used to get the initial EMA value
# input: data for the time period, period num of days being used
# output: intitial EMA value


def get_SMA(data, period):
    smaSum = 0
    for x in data:
        smaSum += data[x]
    return (smaSum/period)

# Get the Exponential moving average of a time period in days used by MACD function
# input: data, period
# output: a sma of that period of time


def get_EMA(closingPrice, lastEmaValue, period):
    weight = 2/(period+1)
    newEMA = ((closingPrice-lastEmaValue)*weight + lastEmaValue*(1-weight))
    return newEMA
