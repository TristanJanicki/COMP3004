# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class NewThresholdExperiment(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """

    def __init__(self, indicator: str=None, threshold: int=None, ticker: str=None, direction_bias: str=None):  # noqa: E501
        """NewThresholdExperiment - a model defined in Swagger

        :param indicator: The indicator of this NewThresholdExperiment.  # noqa: E501
        :type indicator: str
        :param threshold: The threshold of this NewThresholdExperiment.  # noqa: E501
        :type threshold: int
        :param ticker: The ticker of this NewThresholdExperiment.  # noqa: E501
        :type ticker: str
        :param direction_bias: The direction_bias of this NewThresholdExperiment.  # noqa: E501
        :type direction_bias: str
        """
        self.swagger_types = {
            'indicator': str,
            'threshold': int,
            'ticker': str,
            'direction_bias': str
        }

        self.attribute_map = {
            'indicator': 'indicator',
            'threshold': 'threshold',
            'ticker': 'ticker',
            'direction_bias': 'direction_bias'
        }

        self._indicator = indicator
        self._threshold = threshold
        self._ticker = ticker
        self._direction_bias = direction_bias

    @classmethod
    def from_dict(cls, dikt) -> 'NewThresholdExperiment':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The newThresholdExperiment of this NewThresholdExperiment.  # noqa: E501
        :rtype: NewThresholdExperiment
        """
        return util.deserialize_model(dikt, cls)

    @property
    def indicator(self) -> str:
        """Gets the indicator of this NewThresholdExperiment.


        :return: The indicator of this NewThresholdExperiment.
        :rtype: str
        """
        return self._indicator

    @indicator.setter
    def indicator(self, indicator: str):
        """Sets the indicator of this NewThresholdExperiment.


        :param indicator: The indicator of this NewThresholdExperiment.
        :type indicator: str
        """
        if indicator is None:
            raise ValueError("Invalid value for `indicator`, must not be `None`")  # noqa: E501

        self._indicator = indicator

    @property
    def threshold(self) -> int:
        """Gets the threshold of this NewThresholdExperiment.


        :return: The threshold of this NewThresholdExperiment.
        :rtype: int
        """
        return self._threshold

    @threshold.setter
    def threshold(self, threshold: int):
        """Sets the threshold of this NewThresholdExperiment.


        :param threshold: The threshold of this NewThresholdExperiment.
        :type threshold: int
        """
        if threshold is None:
            raise ValueError("Invalid value for `threshold`, must not be `None`")  # noqa: E501

        self._threshold = threshold

    @property
    def ticker(self) -> str:
        """Gets the ticker of this NewThresholdExperiment.


        :return: The ticker of this NewThresholdExperiment.
        :rtype: str
        """
        return self._ticker

    @ticker.setter
    def ticker(self, ticker: str):
        """Sets the ticker of this NewThresholdExperiment.


        :param ticker: The ticker of this NewThresholdExperiment.
        :type ticker: str
        """
        if ticker is None:
            raise ValueError("Invalid value for `ticker`, must not be `None`")  # noqa: E501

        self._ticker = ticker

    @property
    def direction_bias(self) -> str:
        """Gets the direction_bias of this NewThresholdExperiment.


        :return: The direction_bias of this NewThresholdExperiment.
        :rtype: str
        """
        return self._direction_bias

    @direction_bias.setter
    def direction_bias(self, direction_bias: str):
        """Sets the direction_bias of this NewThresholdExperiment.


        :param direction_bias: The direction_bias of this NewThresholdExperiment.
        :type direction_bias: str
        """
        if direction_bias is None:
            raise ValueError("Invalid value for `direction_bias`, must not be `None`")  # noqa: E501

        self._direction_bias = direction_bias
