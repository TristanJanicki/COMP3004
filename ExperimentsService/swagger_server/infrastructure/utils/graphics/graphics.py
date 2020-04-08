from sklearn.tree import export_graphviz
import scipy.stats as st
from scipy.stats import kurtosis
from scipy.stats import skew
import statsmodels.api as sm
import pylab as py
import matplotlib.pyplot as plt
import graphviz
import seaborn as sns
import numpy as np
import graphviz
import subprocess


def plot_correlation_matrix(corr, labels, figure=1):
    plt.figure(figure)
    sns.heatmap(corr,
                xticklabels=labels,
                yticklabels=labels)


def plot_histo(arr, title, x_label, figure=1):
    # matplotlib histogram
    plt.figure(figure)
    plt.hist(arr, color='blue', edgecolor='black',
             bins=int(180/5))

    # seaborn histogram
    sns.distplot(arr, rug=True, hist=True, kde=False,
                 bins=int(180/5), color='blue')

    mean = np.array(arr).mean()
    std_dev = np.array(arr).std()
    decimal_places = 3
    plt.legend(["Std Dev: {0:.3f}".format(round(std_dev, decimal_places)), "N: {0:.3f}".format(
        len(arr)), "Mean: {0:.3f}".format(round(mean, decimal_places))], loc=2)
    # Add labels
    plt.title(title)
    plt.xlabel(x_label)
    plt.ylabel('# Occurances')


def plot_scatter(x, y, x_label, y_label, title, figure=1):
    plt.figure(figure)
    plt.title(title)
    plt.scatter(x, y, color='r')
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.title(title)

def visualize_tree(tree, feature_names):
    """Create tree png using graphviz.

    Args
    ----
    tree -- scikit-learn DecsisionTree.
    feature_names -- list of feature names.
    """
    with open("dt.dot", 'w') as f:
        dot_data = export_graphviz(tree, out_file=f,
                                   feature_names=feature_names,
                                   class_names=["<10%", ">=10%"])

    command = ["dot", "-Tpng", "dt.dot", "-o", "dt.png"]
    try:
        subprocess.check_call(command)
    except:
        exit("Could not run dot, ie graphviz, to "
             "produce visualization")
