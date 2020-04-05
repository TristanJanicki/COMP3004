import pandas as pd
from swagger_server.infrastructure.utils import graphics

from random import random
from random import seed
import time
from datetime import datetime, timedelta

base_path = "../DataService/stock_data/"
# base_path = "C:/Users/trist/Desktop/stock_data/"

def get_currency_delta_distribution(currency_1, currency_2, verbose=False):
    price_deltas = []

    time_series = pd.read_csv(base_path +
                              currency_1 + "_" + currency_2 + "_FXData.csv")

    last_price = None
    for series_data in time_series[::-1].iterrows():
        if last_price == None:
            last_price = float(series_data[1][2])
            continue

        # print(series_data)

        curr_price = float(series_data[1][2])
        delta = ((curr_price - last_price) / last_price) * 100

        last_price = curr_price
        price_deltas.append(delta)

    if verbose == True:
        plot_histo(price_deltas, currency_1 + ":" +
                   currency_2, "% price move day to day")

    return price_deltas

def load_technical_data(ticker, function, extended_function_name=""):
    try:
        # print(os.listdir("../../DataSerivce/stock_data")) # The big database
        # print(os.listdir("../Stock_Data")) # database of personal interest
        path = base_path + ticker + "_" + function + extended_function_name + "Data.csv"
        data = pd.read_csv(path)
        time_period_data = []

        # row[0] is the date of record. row[1] is the value for the technical indicator at the date of record.
        for row in data[::-1].iterrows():
            # print(row)
            # print(data.loc[i])
            if function == "STOCH":
                # , row["SlowK"] TODO: work this so it gives buy and sell signals based on the %d crossing the %k
                time_period_data.append((row[0], row["SlowD"]))
            else:
                # print((row[-1]['time']))
                time_period_data.append((row[-1]['time'], row[-1][function]))
    except Exception as e:
        raise e
    return time_period_data

def get_price_delta_distribution(ticker, year="ALL", verbose=False, figure=1):
    price_deltas = []
    time_series = pd.read_csv(base_path +
                              ticker + "_TIME_SERIES_DAILYData.csv")

    last_price = None
    for series_data in time_series[::-1].iterrows():
        if last_price == None:
            last_price = float(series_data[1][2])
            continue

        # print(series_data)

        curr_price = float(series_data[1][2])
        delta = ((curr_price - last_price) / last_price) * 100

        last_price = curr_price
        price_deltas.append(delta)

    if verbose == True:
        plot_histo(price_deltas, "% Price Moves " +
                   ticker, "% Move", figure=figure)

    return price_deltas

def get_n_days_future_price_delta_with_threshold(ticker, year="ALL", n_days=1, threshold=0, verbose=False, figure=1):
    prices = []
    nPrice_deltas = []
    price_deltas = []
    time_series = pd.read_csv(base_path +
                              ticker + "_TIME_SERIES_DAILYData.csv")

    for series_data in time_series[::-1].iterrows():
        openPrice = float(series_data[1][1])
        closePrice = float(series_data[1][4])
        prices.append((openPrice, closePrice))

    # run until -1 beause we can't get tommorows price action for the most current data point since that is the last day.
    for i in range(1, len(prices) - n_days):
        #### Current Day's Price Action ####
        openPrice = prices[i][0]
        closePrice = prices[i][1]
        delta = ((closePrice - openPrice) / openPrice) * 100

        #### Next Day's Price Action ####

        nOpenPrice = prices[i+n_days][0]
        nClosePrice = prices[i+n_days][1]

        nDelta = ((nClosePrice - nOpenPrice) / nOpenPrice) * 100

        # print(series_data)
        if threshold == 0:
            nPrice_deltas.append(nDelta)
            price_deltas.append(delta)
        elif threshold > 0 and delta >= threshold:
            nPrice_deltas.append(nDelta)
            price_deltas.append(delta)
        elif threshold < 0 and delta <= threshold:
            nPrice_deltas.append(nDelta)
            price_deltas.append(delta)

    if verbose == True:
        corr_matrix = np.corrcoef(price_deltas, nPrice_deltas)
        print(corr_matrix)
        plot_scatter(price_deltas, nPrice_deltas, "delta at t",
                     "delta at t+1", "% Price Moves", figure=figure+2)
        plot_correlation_matrix(
            corr_matrix, ["Move > " + str(threshold) + "%", "Next Day Move"], figure=figure+1)
        plot_histo(price_deltas, ticker + "\n % Price Moves\nDay After a " +
                   str(threshold) + "% Move", "% Move", figure=figure)

    return nPrice_deltas

def sample_randomly(data, n_samples):
    seed(1)
    sample = []
    for i in range(n_samples):
        index = int(random() * len(data))
        sample.append(data[index])
    return sample


def amalgamate_data(*datas):

    amalgamation = []
    # print("len(datas) = ", len(datas))
    for i in range(len(datas)):  # all datas should be the same length!
        amalgamated_row = []
        # print("data: ", i, datas[i])
        # print("len(data)", len(datas[i]))

        for j in range(len(datas[i])):
            # print("amalgamating j = ", j, 'data = ', datas[i][j])
            amalgamated_row.append(datas[i][j])
        # print(amalgamated_row)
        amalgamation.append(amalgamated_row)
    return amalgamation

def get_earliest_date(ticker, technicals, extended_function_name=""):
    earliest_date = None
    time_series = pd.read_csv(base_path +
                              ticker + "_TIME_SERIES_DAILYData.csv", index_col=0)

    for d, _ in time_series[::-1].iterrows():

        earliest_date = d

        # only need the first value. [::-1] makes it start from the earliest date (the last one in the file) so this is the only date we need to compare
        break

    for tech in technicals:
        data = pd.read_csv(base_path + ticker + "_" +
                           tech + extended_function_name + "Data.csv", index_col=0)
        for d, _ in data[::-1].iterrows():

            if earliest_date == None:
                earliest_date = d
            elif d > earliest_date:  # we actually want the lastest earliest date in each file. AKA We need the earliest date the is present in ALL files
                earliest_date = d

            # only need the first value. [::-1] makes it start from the earliest date (the last one in the file) so this is the only date we need to compare
            break

    return earliest_date


def get_latest_date(ticker, technicals, extended_function_name=""):
    latest_date = None
    time_series = pd.read_csv(base_path +
                              ticker + "_TIME_SERIES_DAILYData.csv", index_col=0)

    for d, _ in time_series.iterrows():

        latest_date = d

        # only need the first value. .iterrows() starts from the latest date (the last one in the file) so this is the only date we need to compare
        break

    for tech in technicals:
        data = pd.read_csv(base_path + ticker + "_" +
                           tech + extended_function_name + "Data.csv", index_col=0)
        for d, _ in data.iterrows():

            if latest_date == None:
                latest_date = d
            elif d < latest_date:  # we actually want the lastest earliest date in each file. AKA We need the earliest date the is present in ALL files
                latest_date = d

            # only need the first value. [::-1] makes it start from the earliest date (the last one in the file) so this is the only date we need to compare
            break

    return latest_date


def get_1std_dev(ticker, year, time_period=5):
    time_series = pd.read_csv(base_path +
                              ticker + "_TIME_SERIES_DAILYData.csv", index_col=0)
    price_deltas = []  # clear the price deltas
    i = 0  # clear the counter
    first_price = 0
    second_price = 0

    # reformat the year string to make sure

    last_day_of_year = year.split("-")
    last_day_of_year[1] = "12"  # change the month to december
    # change the day to 31. The last day in the year, so that we caputre the full year of data
    last_day_of_year[2] = "31"

    last_day_of_year = "-".join(last_day_of_year)

    for date, row in time_series[::-1].iterrows():
        if date < year:
            continue
        elif date > last_day_of_year:
            break
        if i == 0:
            first_price = float(row["close"])
        elif i % time_period == 0:

            second_price = float(row["close"])
            # how many percents has the ticker moved since the last time period (that wasn't 0)
            percent_change = (second_price - first_price) / first_price
            price_deltas.append(percent_change)
            first_price = float(row["close"])
        i += 1

    price_deltas = np.array(price_deltas)
    std_dev = price_deltas.std()
    mean = price_deltas.mean()
    return std_dev, mean


def get_prob_of_move(move, mean, std_dev):
    z_score = (move - mean) / std_dev
    p_values = st.norm.sf(abs(z_score))
    return z_score, p_values


def binary_search(arr, l, r, key_index, key):
    while l <= r:

        mid = int(l + (r - l)/2)

        # Check if x is present at mid
        if arr[mid][key_index] == key:
            return mid

        elif arr[mid][key_index] < key:
            l = mid + 1

        else:
            r = mid - 1

    return -1


def test_train_split(x, labels, dates, ticker_data, proportion_to_train_on=0.85):

    train_cutoff = int(len(x) * proportion_to_train_on)

    train_ticker_data = ticker_data[:train_cutoff]
    test_ticker_data = ticker_data[train_cutoff:]
    test_dates = dates[train_cutoff:]
    train_dates = dates[:train_cutoff]
    x_test = x[train_cutoff:]
    y_test = labels[train_cutoff:]
    X = x[:train_cutoff]
    Y = labels[:train_cutoff]

    print("len(X)", len(X))
    print("len(x_test) = ", len(x_test))

    return X, Y, x_test, y_test, train_dates, test_dates, train_ticker_data, test_ticker_data


def get_ai_data(tickers, technicals, time_period=5, percent_movement_threshold=None, std_dev_threshold=None, noise_ratio=2):
    if percent_movement_threshold == None and std_dev_threshold == None:
        print("you must specify a percent_movement_threshold or a std_dev_threshold, suggested values are -0.05% and 1 standard deviation")
        return
    data = []
    total_real_drops = 0
    price_deltas = []
    labels = []
    volumes = []
    dates = []
    ticker_data = []
    i = 0

    for ticker in tickers:
        time_series = pd.read_csv(base_path +
                                  ticker + "_TIME_SERIES_DAILYData.csv", index_col=0)

        first_price = None
        first_date = ""
        technical_data_maps = []

        for technical in technicals:
            technical_data_maps.append(load_technical_data(ticker, technical))

        earliest_date = get_earliest_date(ticker, technicals)
        latest_date = get_latest_date(ticker, technicals)

        if std_dev_threshold != None:
            # if someone inputs 1 then the threshold is 1 std dev, if they input 0.8 its 80% of one std dev, if they input 2 its 2 whole std devs
            std_dev, mean = get_1std_dev(ticker, earliest_date, 1)
            new_std_dev_threshold = std_dev * float(std_dev_threshold)

        for date, row in time_series[::-1].iterrows():
            # print("DATE: ", row[0])
            # print(row)

            if date < earliest_date:
                continue  # keep skipping iterations of this  loop until we get to the first date present in all data files. This way we avoid key errors and matching data from different dates into a single feature vector
            if date > latest_date:
                continue  # after I stopped cleaning the data because of the above check it became apparant that some indicators had data up to a more recent date which produced the same key error

            if first_price == None:
                first_price = float(row["close"])

                first_date = date
                # print("firts_date:", first_date)

            if i % time_period == 0:
                curr_date = date
                # print("first/curr_date:", first_date, curr_date)
                curr_price = float(row["close"])
                percent_move = (curr_price - first_price) / first_price

                # refresh the std dev every year, maybe this helps
                if std_dev_threshold != None and curr_date.split("-")[0] > first_date.split("-")[0]:
                    # if someone inputs 1 then the threshold is 1 std dev, if they input 0.8 its 80% of one std dev, if they input 2 its 2 whole std devs
                    std_dev, mean = get_1std_dev(ticker, earliest_date, 1)
                    new_std_dev_threshold = std_dev * float(std_dev_threshold)

                if percent_movement_threshold != None:

                    if percent_move == 0:
                        percent_move += 0.00000001

                    if percent_movement_threshold > 0 and percent_move >= percent_movement_threshold:
                        total_real_drops += 1
                        label = 1

                    elif percent_movement_threshold < 0 and percent_move <= percent_movement_threshold:
                        # d1 = datetime.strptime(first_date, '%Y-%m-%d')
                        # d2 = datetime.strptime(curr_date, '%Y-%m-%d')
                        # print("percent move =", curr_price, "-", first_price, "/", first_price)
                        # print(ticker + "        ", percent_move, "% move between ", first_date,
                        #   " and ", curr_date, "(", (d2-d1), ")", first_price, curr_price, "rsi: ",rsi_data[first_date], "adx: ", adx_data[first_date], "sar: ", sar_data[first_date], "atr", atr_data[first_date])
                        total_real_drops += 1

                        label = 1
                    else:
                        label = -1
                elif std_dev_threshold != None:

                    if new_std_dev_threshold > 0 and percent_move > new_std_dev_threshold:
                        label = 1
                        total_real_drops += 1
                    elif new_std_dev_threshold < 0 and percent_move < new_std_dev_threshold:
                        # print("appending a move of ", percent_move, "for", ticker)
                        label = 1
                        total_real_drops += 1
                    else:
                        label = -1

                else:
                    label = -1

                # a high noise ratio results in fewer real_drops being mandated to be in the data. E.x. a noise_ratio of 2 means 50 % real drops, 50 % non-real drops (movements less that do not meet the threshold requirement)
                # only add data points if its a real drop data point or if theres less than 2 * the total number of total drops in data
                if len(data) < noise_ratio * total_real_drops or label == 1:

                    ticker_data.append(ticker)
                    labels.append(label)
                    volumes.append(data["volume"])
                    # print("appending", ticker, "first_date: ", first_date, " curr_date:", curr_date, [rsi_data[first_date], adx_data[first_date], sar_data[first_date], atr_data[first_date]])
                    dates.append((first_date, curr_date))

                    feature_vector = []
                    exception_occured = False

                    for technical_map in technical_data_maps:

                        index = binary_search(technical_map, 0, len(
                            technical_map), 0, first_date)

                        if index != -1:
                            feature_vector.append(technical_map[index])

                    data.append(feature_vector)

                first_price = curr_price
                first_date = curr_date

                # print(percent_move)
            i += 1

    print("total_real_drops (in all data): ", total_real_drops,
          "out of a total of", len(data), "data points")
    # print("len(ticker_data): ", len(ticker_data))
    # print("len = ", len(price_deltas))
    return data, labels, dates, ticker_data, volumes
