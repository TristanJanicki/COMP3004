import requests
import json
import matplotlib
import matplotlib.pyplot as plt
from datetime import datetime, timedelta

def get_options_commissions(price_per_option, num_contracts, strike_price, brokerage="TD"):
    
    if brokerage == "TD": # TD means TD 
        per_contract_cost = 1.25
        per_trade_cost = 9.99
    elif brokerage == "TDA": # TDA means TD Ameritrade
        per_contract_cost = 0.65
        per_trade_cost = 0.0

    comission_per_share = (((per_trade_cost * 2) + per_contract_cost  * num_contracts) / (num_contracts * 100) ) + price_per_option # TD Bank charges comissions of $9.99 per trade and $1.25 per options contract
    total_comission = per_trade_cost + (per_contract_cost * num_contracts) + (price_per_option * num_contracts * 100)
    breakEven = strike_price - comission_per_share
    return comission_per_share, total_comission, breakEven

def get_put_profits(price_per_option, num_contracts, strike_price, price_at_expiration):
    cps, tc, breakEven = get_options_commissions(price_per_option, num_contracts, strike_price, brokerage="TDA")
    
    profit_per_share = (breakEven - price_at_expiration)
    total_profits = profit_per_share * (num_contracts * 100)
    reward_risk_ratio = total_profits / tc
    return (profit_per_share, total_profits, breakEven, reward_risk_ratio)

def get_call_profits(price_per_option, num_contracts, strike_price, price_at_expiration):
    
    breakEven = strike_price + get_options_commissions(price_per_option, num_contracts, price_at_expiration)[0] # get the comissions per share
    profit_per_share = (price_at_expiration - breakEven)
    total_profit = profit_per_share * num_contracts * 100
    return profit_per_share, total_profit, breakEven

def sortOptionsByComission(elem):
    return elem[3]

def sortOptionsByBreakEven(elem):
    return elem[4]

def sortOptionsByRewardToRisk(elem):
    return elem[5] # sort by return to risk ration (the 5th element in the options array)

def sortOptionsByTotalProfit(elem):
    return elem[6]

def sortOptionsByRequiredMoveToBreakEven(elem):
    return elem[7]

def get_options_chain(ticker, option_type="PUT", option_range="NTM", strike_count=3, days_til_expiry=5):
    
    options = []
    delta = timedelta(days_til_expiry)
    # to_date = datetime.strptime((datetime.now() + delta), '%Y-%m-%d')
    to_date = datetime.now() + delta
    to_date = to_date.strftime("%Y-%m-%d")

    r = requests.get("https://api.tdameritrade.com/v1/marketdata/"+ticker+"/quotes?&apikey=JPGIHQGE5ZUUQAEVAKT6JDKWM8WAALL2")

    response = json.loads(r.content)

    last_price = response[ticker]["mark"]
    r = requests.get("https://api.tdameritrade.com/v1/marketdata/chains?apikey=JPGIHQGE5ZUUQAEVAKT6JDKWM8WAALL2&strikeCount="+str(strike_count)+"&symbol="+ticker+"&contractType="+option_type+"&range="+option_range+"&daysToExpiration="+str(days_til_expiry))
    response = json.loads(r.content)

    for expiry_date in response[option_type.lower()+"ExpDateMap"]:
        for strike_price in response[option_type.lower()+"ExpDateMap"][expiry_date]:
            for option in response[option_type.lower()+"ExpDateMap"][expiry_date][strike_price]:
                description = option["description"]
                ask = option["ask"]
                strike_price = option["strikePrice"]
                
                cps, total_comission, breakEven = get_options_commissions(ask, 1, strike_price, brokerage="TDA")
                price_at_expiration = breakEven * 0.99 # assume 1% profit
                pps, total_profit, be, rrr = get_put_profits(ask, 1, strike_price, price_at_expiration) 
                # pps is profit per share, rrr is risk reward ratio, be is break even (again), total profit is the total profit

                required_move_from_last_price = 1 - (breakEven / float(last_price))

                options.append([description, ask, strike_price, total_comission, be, rrr, total_profit, required_move_from_last_price])

    return options

def printChain(oChain):
    for o in oChain:
        print("Desc {}:\tAsk: {} \tStrike Price: {} \t Total Comission: {:5.2f} \t Break Even: {:5.2f} \t Reward Risk Ratio: {:5.2f} \t Total Profit: {:5.2f} \t % Move to break even {:5.5f}".format(o[0], o[1], o[2], o[3], o[4], o[5], o[6], o[7]))
        # print("Desc:", o[0], "Ask:", o[1], "Strike Price:", o[2], "Total Comission:", o[3], "Break Even:", o[4] , "Reward Risk Ratio:", o[5], "Total Profit:", o[6])
    print("\n\n")


# TODO: convert the two functions below to call the get_options_change function and use the Greek values returned by it to effect the change in premium. The use the list of options chain to get the 
# change in gamma since that isn't always 0.01 but I couldn't find any info on how gamma changes just that its a second order derrivate of the price. I guess that means its the change of the change
# which would be the delta of delta but I'm not sure. :shurgs: Also I don't know how to calculate a second derivative.
# Note: I'm using the work premium here not price_per_option because that would be a very long name
def modelCallOptionPriceChange(share_price, premium, delta, gamma, theta, vega):
    option_value_history = [premium]
    for i in range(1, 101): # the full range of price movements for a stock, it can go 100% in either direction (well not really but there's no use in testing infiniti possible upward movements):
        percent_change = i / 100
        dollar_change = percent_change * share_price
        premium_change = dollar_change * delta
        delta = delta + gamma
        gamma = gamma + 0.01
        premium_change = premium_change - theta
        option_value_history.append(premium_change)
    return option_value_history

def modelPutOptionPriceChange(share_price, premium, delta, gamma, theta, vega):
    option_value_history = [premium]
    for i in range(1, 101): # the full range of price movements for a stock, it can go 100% in either direction (well not really but there's no use in testing infiniti possible upward movements):
        percent_change = i / 100
        dollar_change = percent_change * share_price
        premium_change = dollar_change * delta
        delta = delta - gamma
        gamma = gamma - 0.01
        premium_change = premium_change - theta
        premium = premium + premium_change
        option_value_history.append(premium)
    return option_value_history

if __name__ == '__main__':
    # costs = get_options_commissions(0.56, 50, 314.5)
    # print("Total Comission", costs[1])
    # profits = get_call_profits(0.56, 50, 314, costs[2] * 1.003)
    # print("PPS: ", profits[0], "Total Profits:", profits[1], "Break Even:", profits[2])

    # exit(1)

    strategies = ["SINGLE", "ANALYTICAL", "COVERED", "VERTICAL", "CALENDAR", "STRANGLE", "STRADDLE", "BUTTERFLY", "CONDOR", "DIAGONAL", "COLLAR", "ROLL"]
    
    oChain = get_options_chain("XLE", option_type="PUT", days_til_expiry=15)
    # oChain.sort(key=sortOptionsByRewardToRisk, reverse=True)
    # print("Sorted By Reward to Risk:")
    # printChain(oChain)
    # oChain.sort(key=sortOptionsByTotalProfit, reverse=True)
    # print("Sorted By Total Profit:")
    # printChain(oChain)
    print("Sorted by % move to break even")
    oChain.sort(key=sortOptionsByRequiredMoveToBreakEven)
    printChain(oChain)

    exit(1)

    profit_history = []
    profit_per_share_history = []
    comission_history = []
    commision_per_share_history = []
    liquidity_history = []
    risk_reward_history = []

    assumed_profit = 0.01 # let's assume one percent profit
    strike_price = 76
    # price_at_expiration = strike_price*(1-assumed_profit)
    price_per_option = 1.65
    num_contracts = 6



    for i in range(1, num_contracts + 1):
        # i is the current number of contracts we intend to buy. With this we can get a graph of how much profit we'd make buying i options contracts and at what risk.
        cps, total_comission, breakEven = get_options_commissions(price_per_option, i, strike_price)
        price_at_expiration = (1 - assumed_profit) * breakEven
        commision_per_share_history.append(cps)
        comission_history.append(total_comission)
        liquidity_history.append(i * 100 * price_at_expiration)
        pps, total_profit, be, rrr = get_put_profits(price_per_option, i, strike_price, price_at_expiration)
        print('Total Profit for ', i, ' contracts is ', total_profit)
        print('Total Commission for ', i, ' contracts is ', total_comission)
        print('Break Even for ', i, ' contracts is ', breakEven)
        print('Total Liquidity needed for ', i, ' contracts is ', liquidity_history[-1])
        print('Reward to Risk for ', i, ' contracts is ', rrr)
        print()
        profit_per_share_history.append(pps)
        profit_history.append(total_profit)

    fig, ax = plt.subplots()

    plt.subplot(2, 2, 1)
    plt.xlabel('# options bought')
    plt.ylabel('total profit')
    ax.annotate(profit_history[0], (profit_history[0], 1))
    plt.plot(range(1, num_contracts + 1), profit_history)


    plt.subplot(2, 2, 2)
    plt.xlabel('# options bought')
    plt.ylabel('profit per share')
    plt.plot(range(1, num_contracts + 1), profit_per_share_history)


    plt.subplot(2, 2, 3)
    plt.xlabel('# options bought')
    plt.ylabel('comission per share')   
    plt.plot(range(1, num_contracts + 1), commision_per_share_history)


    plt.subplot(2, 2, 4)
    plt.xlabel('# options bought')
    plt.ylabel('total comission')
    plt.plot(range(1, num_contracts + 1), comission_history)

    plt.figure(2)
    plt.subplot(2, 2, 1)
    plt.xlabel('# options bought')
    plt.ylabel('$ liquidity needed')
    plt.plot(range(1, num_contracts + 1), liquidity_history)

    plt.show()

    # make a legend
    # get a chart for theta (might just be one number)