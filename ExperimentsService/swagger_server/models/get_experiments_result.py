# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server.models.existing_correlation_experiment import ExistingCorrelationExperiment
from swagger_server.models.existing_threshold_experiment import ExistingThresholdExperiment
from swagger_server import util


class GetExperimentsResult(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """

    def __init__(self, correlations: List[ExistingCorrelationExperiment]=None, thresholds: List[ExistingThresholdExperiment]=None):  # noqa: E501
        """GetExperimentsResult - a model defined in Swagger

        :param correlations: The correlations of this GetExperimentsResult.  # noqa: E501
        :type correlations: List[ExistingCorrelationExperiment]
        :param thresholds: The thresholds of this GetExperimentsResult.  # noqa: E501
        :type thresholds: List[ExistingThresholdExperiment]
        """
        self.swagger_types = {
            'correlations': List[ExistingCorrelationExperiment],
            'thresholds': List[ExistingThresholdExperiment]
        }

        self.attribute_map = {
            'correlations': 'correlations',
            'thresholds': 'thresholds'
        }

        self._correlations = correlations
        self._thresholds = thresholds

    @classmethod
    def from_dict(cls, dikt) -> 'GetExperimentsResult':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The getExperimentsResult of this GetExperimentsResult.  # noqa: E501
        :rtype: GetExperimentsResult
        """
        return util.deserialize_model(dikt, cls)

    @property
    def correlations(self) -> List[ExistingCorrelationExperiment]:
        """Gets the correlations of this GetExperimentsResult.


        :return: The correlations of this GetExperimentsResult.
        :rtype: List[ExistingCorrelationExperiment]
        """
        return self._correlations

    @correlations.setter
    def correlations(self, correlations: List[ExistingCorrelationExperiment]):
        """Sets the correlations of this GetExperimentsResult.


        :param correlations: The correlations of this GetExperimentsResult.
        :type correlations: List[ExistingCorrelationExperiment]
        """

        self._correlations = correlations

    @property
    def thresholds(self) -> List[ExistingThresholdExperiment]:
        """Gets the thresholds of this GetExperimentsResult.


        :return: The thresholds of this GetExperimentsResult.
        :rtype: List[ExistingThresholdExperiment]
        """
        return self._thresholds

    @thresholds.setter
    def thresholds(self, thresholds: List[ExistingThresholdExperiment]):
        """Sets the thresholds of this GetExperimentsResult.


        :param thresholds: The thresholds of this GetExperimentsResult.
        :type thresholds: List[ExistingThresholdExperiment]
        """

        self._thresholds = thresholds
