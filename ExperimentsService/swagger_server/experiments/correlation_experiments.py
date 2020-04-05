from swagger_server.infrastructure.utils.data_tools.data_tools import *

import numpy as np

def getAssetCorrelation(asset_1, asset_2, asset_combo):
    if asset_combo == "equity_equity":
        d1 = get_price_delta_distribution(asset_1)
        d2 = get_price_delta_distribution(asset_2)
    elif asset_combo == "equity_currency":
        d1 = get_price_delta_distribution(asset_1)
        currency_1 = asset_2.split(":")[0]
        currency_2 = asset_2.split(":")[1]
        d2 = get_currency_delta_distribution(currency_1, currency_2)
    elif asset_combo == "currency_currency":
        currency_11 = asset_1.split(":")[0]
        currency_12 = asset_1.split(":")[1]
        d1 = get_currency_delta_distribution(currency_11, currency_12)
        currency_21 = asset_2.split(":")[0]
        currency_22 = asset_2.split(":")[1]
        d2 = get_currency_delta_distribution(currency_21, currency_22)
    elif asset_combo == "currency_equity":
        currency_1 = asset_1.split(":")[0]
        currency_2 = asset_1.split(":")[1]
        d1 = get_currency_delta_distribution(currency_1, currency_2)
        d2 = get_price_delta_distribution(asset_2)
    else:
        raise Exception(
            "Invalid asset_combo {%s} passed to get asset correlation" % (asset_combo))
    if len(d1) != len(d2):
        new_length = min(len(d1), len(d2))
        d1 = sample_randomly(d1, new_length)
        d2 = sample_randomly(d2, new_length)
    corr_matrix = np.corrcoef(d1, d2)
    return corr_matrix, d1, d2

