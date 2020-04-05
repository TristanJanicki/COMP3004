from swagger_server.infrastructure.utils.data_tools.data_tools import *
from swagger_server.infrastructure.utils import graphics

import threading
import concurrent
from concurrent.futures import ThreadPoolExecutor, ProcessPoolExecutor
import numpy as np
import scipy.stats as st

def get_rsi_threshold_move_distribution(tickers, year, rsi_threshold, days_from_inversion=1, verbose=False, figure=1, directional_bias="crosses_below"):
    price_deltas = []
    volumes = []

    for ticker in tickers:
        days_above_threshold = 0

        data = load_technical_data(ticker, "RSI", "14dailyclose")

        time_series = pd.read_csv(base_path +
                                  ticker + "_TIME_SERIES_DAILYData.csv")
        prices = []
        volumes = []
        event_dates = []

        prev_date = ""

        earliest_date = get_earliest_date(ticker, ["RSI"], "14dailyclose")
        latest_date = get_latest_date(ticker, ["RSI"], "14dailyclose")
        last_rsi = ""
        iteration_count = 0

        for series_data in time_series[::-1].iterrows():
            # print(series_data)
            prices.append(float(series_data[1][2]))
            volumes.append(float(series_data[1][5]))

        for i in range(len(data)):
            date = data[i][0]
            # print(data)
            iteration_count += 1
            if data[i][0] < earliest_date:  # skip the first item so we don't get a 0 value
                prev_date = date.split(" ")[0]
                index = binary_search(data, 0, len(data), 0, date)
                if index != -1:
                    last_rsi = data[index][1]
                continue

            if data[i][0] > latest_date:
                break

            if date.split("-")[0] >= year or year == "ALL":
                # get the RSI value on the date parameter
                index = binary_search(data, 0, len(data), 0, date)
                if index != -1:

                    if index + days_from_inversion > len(data):
                        continue

                    if data[index-1][1] > rsi_threshold and data[index][1] < rsi_threshold:
                        try:
                            # print(volumes[index-1])
                            one_day_ago_volume = volumes[index-1]
                            two_days_ago_volume = volumes[index-2]
                            three_days_ago_volume = volumes[index-3]

                            three_day_avg_volume = (
                                three_days_ago_volume + two_days_ago_volume + one_day_ago_volume) / 3
                            price_delta = 100 * \
                                ((prices[index] - prices[index +
                                                         days_from_inversion]) / prices[index])

                            # print("pd: ", price_delta)
                            # print("days_above_threshold: ", days_above_threshold)
                            # print("three_day_avg_volume: ", three_day_avg_volume)
                            event_dates.append(date)
                            price_deltas.append(price_delta)
                            volumes.append(three_day_avg_volume)

                        except Exception as e:
                            print(e)

            prev_date = date.split(" ")[0]

    # Setting all these to 0 so returning them doesn't throw an error since sometimes the arrays used to determine them are empty.
    price_deltas = np.array(price_deltas)
    volumes = np.array(volumes)
    price_delta_std_dev = 0
    price_delta_mean = 0
    volumes_mean = 0
    volumes_std_dev = 0
    skewedness = skew(price_deltas)
    kurt = kurtosis(price_deltas)
    if len(price_deltas) > 0 and len(volumes) > 0:
        price_delta_std_dev = price_deltas.std()
        price_delta_mean = price_deltas.mean()
        volumes_mean = volumes.mean()
        volumes_std_dev = volumes.std()

        amalgamation = amalgamate_data(price_deltas, volumes)

        if verbose == True:
            print("latest date: ", latest_date)
            print("earliest date: ", earliest_date)
            print("len(price_deltas):", len(price_deltas))

            print("Std_Dev Of Price Deltas:", price_delta_std_dev)

            print("Mean of Price Deltas:", price_delta_mean)
            print("corrletation of the length of > " + str(rsi_threshold) +
                  " RSI to returns")

            plot_histo(price_deltas, 'Histogram of Price Deltas ' + str(days_from_inversion) + " days from inversion",
                       '%Price Change', figure=figure+2)

            plt.show()
    price_delta_mode = st.mode(price_deltas)
    return price_deltas, price_delta_std_dev, price_delta_mean, price_delta_mode, volumes, volumes_mean, event_dates, kurt, skewedness


def get_optimal_rsi_days_from_inversion(ticker, year="2019", rsi_threshold=70, verbose=False, figure=1):
    max_corr = 0
    best_threshold = 0
    # intuition tells me theres no way that an rsi inversion is even remotely relevant > 14 days later.
    for i in range(0, 14):
        _, _, _, price_deltas, _, _, _, _, _, _ = get_rsi_threshold_move_distribution(
            ticker, year, rsi_threshold, i, False, figure=figure+1)
        corr = np.corrcoef(i, price_deltas)
        if abs(corr[0][0]) > max_corr:
            max_corr = abs(corr[0][0])
            best_threshold = i
    print("Best Correlation(", max_corr,
          ") was for Days From Inversion = ", best_threshold)
    return best_threshold


def get_optimal_rsi_threshold(ticker, year="ALL", days_from_inversion_threshold=1, verbose=False, figure=1):
    pd_mean_history = []
    threshold_history = []
    dispersion_history = []
    # only 100 possible values for the RSI indicator so thats all we need to loop for. Starting at 10 because the rsi is never at 0.
    for i in range(10, 100):
        _, _, _, _, price_delta_std_dev, price_delta_mean, _, _, _ = get_rsi_threshold_move_distribution(
            [ticker], year, i, days_from_inversion_threshold, False, figure=figure+1)
        variance = price_delta_std_dev ** 2

        if variance != 0 and price_delta_mean != 0:
            dispersion_history.append((variance / price_delta_mean))
        else:
            dispersion_history.append(0)
        threshold_history.append(i)
        pd_mean_history.append(price_delta_mean)

    corr = np.corrcoef(amalgamate_data(
        pd_mean_history, dispersion_history, threshold_history))
    if verbose == True:
        plot_scatter(threshold_history, pd_mean_history,
                     "RSI Threshold", "Mean % Move", 1)
        plot_scatter(threshold_history, dispersion_history, "RSI Threshold",
                     "Dispersion Index of % Moves", "Dispersion Indexes", 2)
        plot_correlation_matrix(
            corr, ["Price Delta History", "Dispersion History", "Threshold History"], 3)
        print("Correlation: ", corr)


def get_all_rsi_price_distributions(ticker="AMD", direction_bias="bearish", saveToDataBase=False, callback=None):
    test_results = []
    # with ProcessPoolExecutor as pExec: #TODO implement this one too, 1 process per core, (1 / n_cores) * n_jobs, jobs per process
    with ThreadPoolExecutor(max_workers=1000) as executor:
        all_price_deltas = get_price_delta_distribution(
            ticker, verbose=False, figure=1)
        np_arr = np.array(all_price_deltas)
        all_prices_std_dev = np_arr.std()
        all_price_deltas_mean = np_arr.mean()
        # keep the sample size below 5,000 to avoid p-value warning
        shapiro_w1, shapiro_p1 = st.shapiro(
            all_price_deltas[0:min(len(all_price_deltas), 4000)])

        for threshold in range(0, 100):

            # plot_histo(rsi_price_deltas, "RSI Crosses Below %d Moves", "% Moves" % (i), 4)

            def doWork():
                rsi_price_deltas, price_delta_std_dev, price_delta_mean, price_delta_mode, volumes, volumes_mean, event_dates, skewness, kurtosis = get_rsi_threshold_move_distribution(
                    [ticker], "ALL", threshold, 1, verbose=False, directional_bias=direction_bias)
                if len(rsi_price_deltas) < 3:
                    print("PRICE DELTAS FOR ", threshold, rsi_price_deltas)
                    return threshold, 0, 0, 0, 0, rsi_price_deltas, price_delta_std_dev, price_delta_mean, volumes, volumes_mean, event_dates, 0

                t_test_t, t_test_p = st.ttest_ind(sample_randomly(
                    all_price_deltas, len(rsi_price_deltas)), rsi_price_deltas, equal_var=False)

                shapiro_w2, shapiro_p2 = st.shapiro(rsi_price_deltas)

                # print("##################################################################   TEST RESULTS FOR %d ##################################################################" % (i))
                # print("Population_N: ", len(all_price_deltas), "Sample_N: ", len(rsi_price_deltas))
                # print("Shapiro Test Results")
                # print("Population W: ", shapiro_w1, " P: ", shapiro_p1)
                # print("Sample     W: ", shapiro_w2, " P: ", shapiro_p2)

                # print("T Test Results")
                # print("t = ", t_test_t)
                # print("p = ", t_test_p)

                if saveToDataBase == True and callback != None:
                    callback(int(threshold), float(t_test_t), float(t_test_p), float(shapiro_w2), float(shapiro_p2), rsi_price_deltas.tolist(), float(price_delta_std_dev), float(
                        price_delta_mean), volumes.tolist(), float(volumes_mean), event_dates, direction_bias, float(price_delta_mode[0]), skewness, kurtosis)

            future = executor.submit(doWork)
            res = future.result()
            if res != None:
                test_results.append(res)

        def get_test_result_p(elem):
            return elem[1]

        def get_test_result_n(elem):
            return elem[3]

        test_results.sort(key=get_test_result_p)
        print(test_results)
        print("All Std Dev", all_prices_std_dev)
        print("All Mean:", all_price_deltas_mean)

        return test_results


def get_MACD_threshold_move_distribution(tickers, year, macd_threshold):
    # todo: complete the MACD calculation
    return ""

def test_many_scenarios(tickers, list_of_rsi, days_from_inv_range):
    figure = 1
    subplot_index = 1
    scenario_results = []
    records = []
    futures = []
    for i in list_of_rsi:  # n different RSI thresholds
        # m different inversion thresholds, we don't need a start for these because it
        for j in range(1, days_from_inv_range + 1):

            with concurrent.futures.ThreadPoolExecutor() as executor:
                future = executor.submit(
                    get_rsi_threshold_move_distribution, tickers, "ALL",  1 + i, j, False)
                futures.append(future)
                moves_distribution = future.result()[0]
                move_distribution_delta_mean = future.result()[1]
                import math
                row_and_col = math.sqrt(len(list_of_rsi))
                # print("sub", subplot_index)
                plt.subplot(row_and_col, row_and_col, subplot_index)

                # shapiro test requires a length of at least 3.
                if len(moves_distribution) < 3:
                    continue

                # print("RSI = ", str(i), "dI = ", str(j), "W = ", w, " P = ", p)
                plot_histo(moves_distribution, "RSI = " + str(i) +
                           "dI = " + str(j), "% Move", figure)

                if subplot_index % 25 == 0:
                    figure += 1
                    plt.figure(figure)
                    subplot_index = 0
                subplot_index += 1

    return scenario_results

