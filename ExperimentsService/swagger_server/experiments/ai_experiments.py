from sklearn.naive_bayes import GaussianNB
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import BaggingClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.tree import DecisionTreeClassifier
from sklearn.tree import DecisionTreeRegressor
from sklearn import tree
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import time
from datetime import datetime, timedelta
from sklearn import svm
from sklearn import metrics

from swagger_server.infrastructure.utils.data_tools import *

def train_and_plot_svm(X, y):
    # now transform the old training data into just two dimensions since it seems that SVM can only plot two in two dimensions.
    # also 2 dimensions (adx, sar) are showing to be the most important with feature importances of 0.922 and 0.07 respectively

    for i in range(len(X)):
        X[i] = X[i][1:3]
        # print("new X[i].shape: ", len(X[i]))

    X = np.array(X)
    y = np.array(y)
    h = .02  # step size in the mesh

    # we create an instance of SVM and fit out data. We do not scale our
    # data since we want to plot the support vectors
    C = 1.0  # SVM regularization parameter
    start = time.time()
    svc = svm.SVC(kernel='linear', C=C, gamma="scale").fit(X, y)
    end = time.time()
    print("svm (linear kernel) train time: ", (end-start))
    start = time.time()
    rbf_svc = svm.SVC(kernel='rbf', C=C, gamma="scale").fit(X, y)
    end = time.time()
    print("svm (rbf kernel) train time: ", (end-start))
    start = time.time()
    poly_svc = svm.SVC(kernel='poly', degree=3, C=C, gamma="scale").fit(X, y)
    end = time.time()
    print("svm (poly kernel) train time: ", (end-start))
    start = time.time()
    lin_svc = svm.LinearSVC(C=C).fit(X, y)
    end = time.time()
    print("linear svc train time: ", (end-start))
    start = time.time()
    svr = svm.SVR(gamma="scale", max_iter=1000).fit(X, y)
    end = time.time()
    print("svr train time: ", (end-start))

    # create a mesh to plot in
    x_min, x_max = X[:, 0].min() - 1, X[:, 0].max() + 1
    print("x_min", x_min, "x_max: ", x_max)
    y_min, y_max = X[:, 1].min() - 1, X[:, 1].max() + 1
    print("y_min: ", y_min, "y_max: ", y_max)
    xx, yy = np.meshgrid(np.arange(x_min, x_max, h),
                         np.arange(y_min, y_max, h))

    # print("xx: ", xx)
    # print("yy: ", yy)

    # title for the plots
    titles = ['SVC with linear kernel',
              'LinearSVC (linear kernel)',
              'SVC with RBF kernel',
              'SVC with polynomial (degree 3) kernel',
              'SVR']

    svms = (svc, lin_svc, rbf_svc, poly_svc, svr)
    for i, clf in enumerate(svms):
        print("plotting SVC object: ", i + 1, "/", len(svms))
        # Plot the decision boundary. For that, we will assign a color to each
        # point in the mesh [x_min, x_max]x[y_min, y_max].
        plt.subplot(3, 2, i + 1)
        plt.subplots_adjust(wspace=0.4, hspace=0.4)

        # print("xx.ravel(): ", xx.ravel())
        # print("X.shape[1]: ", xx.shape[1])
        Z = clf.predict(np.c_[xx.ravel(), yy.ravel()])

        # Put the result into a color plot
        Z = Z.reshape(xx.shape)
        plt.contourf(xx, yy, Z, cmap=plt.cm.coolwarm, alpha=0.8)

        # Plot also the training points
        plt.scatter(X[:, 0], X[:, 1], c=y, cmap=plt.cm.coolwarm)
        plt.xlim(xx.min(), xx.max())
        plt.ylim(yy.min(), yy.max())
        plt.xticks(())
        plt.yticks(())
        plt.title(titles[i])

    print("About to show plt")
    plt.show()

def test(clf, x_test, y_test, test_dates, test_ticker_data, verbose=False):
    true = 0
    false = 0
    real_drops = 0

    true_negative = 0
    true_postive = 0
    false_negative = 0
    false_positive = 0

    tp_history = []
    tn_history = []
    fp_history = []
    fn_history = []

    # print("len(ticker_data)", len(test_ticker_data))
    # print("len(x_test)", len(x_test))

    for i in range(len(x_test)):
        # print(x)
        # print("proper label = ", x[-1])
        res = clf.predict(np.array(x_test[i]).reshape(1, -1))[0]
        res_prob = clf.predict_proba(np.array(x_test[i]).reshape(1, -1))[0]

        if res == -1:
            res_prob = res_prob[0]
        else:
            res_prob = res_prob[1]

        if res_prob < 0.8 and res == 1:
            res = 0

        if y_test[i] == 1:
            real_drops += 1

        if res == 1 and y_test[i] == -1:
            false_positive += 1
            fp_history.append(res_prob)
        elif res == -1 and y_test[i] == 1:
            false_negative += 1
            fn_history.append(res_prob)
        elif res == -1 and y_test[i] == -1:
            tn_history.append(res_prob)
            true_negative += 1
        elif res == 1 and y_test[i] == 1:
            tp_history.append(res_prob)
            true_postive += 1

        if res == y_test[i]:
            true += 1
        else:
            false += 1

    if verbose == True:
        print()
        print()
        print()

        print("total real drops (in test data): ", real_drops)
        print("True: ", true)
        print("False: ", false)

        print("True Negatives:", true_negative)
        print("True Positive:", true_postive)
        print("False Negative:", false_negative)
        print("False Positive:", false_positive)

    tp_fn = true_postive + false_negative
    if true_postive != 0 and (tp_fn != 0):
        sensitivity = (true_postive / (tp_fn)) * 100
    else:
        sensitivity = 0

    fp_tv = false_positive + true_negative
    if true_negative != 0 and fp_tv != 0:
        specificity = (true_negative / fp_tv) * 100
    else:
        specificity = 0

    tp_fp = true_postive + false_positive
    if tp_fp != 0:
        positive_predictive_value = (true_postive / (tp_fp)) * 100
    else:
        positive_predictive_value = 0

    fn_fp = false_negative + false_positive

    if fn_fp != 0:
        negative_predictive_value = (true_negative / (fn_fp)) * 100
    else:
        negative_predictive_value = 0
    if verbose == True:
        print()
        print()
        print()

        print("Sensitivity: ", sensitivity)
        print("Specificity: ", specificity)
        print("Positive Predictive Value: ", positive_predictive_value)
        print("Negative Predictive Value: ", negative_predictive_value)

        print("Overall Accuracy(the ability to ignore non-threshold moves and predict threshold moves): ",
              (true / (true + false)))

    fn_history = np.array(fn_history)
    tn_history = np.array(tn_history)
    fp_history = np.array(fp_history)
    tp_history = np.array(tp_history)

    print("fn_history std_dev:", fn_history.std())
    print("fn_history mean:", fn_history.mean())

    print("tn_history std_dev:", tn_history.std())
    print("tn_history mean:", tn_history.mean())

    print("fp_history std_dev:", fp_history.std())
    print("fp_history mean:", fp_history.mean())

    print("tp_history std_dev:", tp_history.std())
    print("tp_history mean:", tp_history.mean())

    return true, false, real_drops, true_negative, true_postive, false_negative, false_positive, sensitivity, specificity, positive_predictive_value, negative_predictive_value

# this function was extracted during the refractoring process that occured on April 5th 2020.
# It originated from random idea testing and is kind just floating waiting for some real purpose somewhere else in the code base.
def WIP_testing():
    # all_price_deltas = get_price_delta_distribution_with_threshold("AMD", threshold=0, verbose=True, figure=2)
    # next_day_price_deltas = get_next_day_price_delta_with_threshold("SPY", threshold=4, verbose=False, figure=3)

    # get_optimal_rsi_threshold("AMD")

    # get_optimal_rsi_days_from_inversion("AMD")

    # sub_sample = sample_randomly(all_price_deltas, len(all_price_deltas))
    # plot_histo(all_price_deltas, "All Moves", "% Moves", 1)
    # plot_histo(sub_sample, "Subsample Moves", "% Moves", 2)
    decision_tree = DecisionTreeClassifier(
        criterion="entropy", min_samples_split=20, random_state=99)
    guassian = GaussianNB()
    random_forest = RandomForestClassifier(
        n_estimators=10)  # BEST FOR SPY ===> 75%
    knn_bagger = BaggingClassifier(KNeighborsClassifier(
    ), max_samples=0.2, max_features=0.2, n_estimators=15)
    tree_bagger = BaggingClassifier(DecisionTreeClassifier(
        criterion="entropy", min_samples_split=20, random_state=99), max_samples=1.0, n_estimators=15)

    models = [decision_tree, guassian, random_forest, knn_bagger, tree_bagger]

    ###########################################################
    train_tickers = ["ACB", "BKNG", "BIDU", "CGC", "CSIQ", "GRUB",
                     "MSFT", "ROKU", "SHOP", "TSLA", "TWLO", "WIX", "ZBRA"]
    # technicals = ["ADX", "ATR", "AROONOSC", "SAR", "RSI", "ROCR", "MFI", "TRIX", "PPO"]
    # TODO: Change the periods used in the PPO technical to reflect my short term trading, maybe 1, 2, 3 day or 1, 3, 5?
    technicals = ["ADX", "ATR", "AROONOSC", "SAR",
                  "RSI", "ROCR", "MFI", "TRIX", "PPO"]

    x, labels, dates, ticker_data = get_ai_data(
        train_tickers, technicals, time_period=5, std_dev_threshold=-2, noise_ratio=4)
    X, Y, _, _, train_dates, _, _, _ = test_train_split(
        x, labels, dates, ticker_data, 1)

    test_tickers = ["APH", "APHA"]
    x_test, y_test, test_dates, test_ticker_data = get_data(
        test_tickers, technicals, time_period=5, std_dev_threshold=-1.5, noise_ratio=4)

    print("First/Last Test Date: ", test_dates[0], test_dates[-1])
    print("Training on ", train_tickers, " data")
    print("Testing on ", test_tickers, " data")

    for clf in models:
        clf.fit(X, Y)
        # print("Feature Importance: ", clf.feature_importances_)
        # the line below tests the model and returns a BUNCH of testing values, hence the line being so big.
        print()
        print("Test Values For: ", clf.__class__)
        true, false, real_drops, true_negative, true_positive, false_negative, false_positive, sensitivity, specificity, positive_predictive_value, negative_predictive_value = test(
            clf, x_test, y_test, test_dates, test_ticker_data, verbose=False)
        print("Sensitivity ", sensitivity)
        print("Real Drops ", real_drops)
        print("True Positives (Profitable Trades)", true_positive)
        print("False Positives (Incorrect Predictions, Lost Money)", false_positive)
        print("Profitable Trades/Money Loosers",
              ((true_positive + 0.0000000001)/(false_positive + 0.0000000001)))
        print("False Negatives (Opportunity Cost)", false_negative)
    ###########################################################
