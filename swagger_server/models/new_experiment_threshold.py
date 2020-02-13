# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class NewExperimentThreshold(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """

    def __init__(self, user_id: str=None, indicator: str=None, threshold: int=None, ticker: str=None):  # noqa: E501
        """NewExperimentThreshold - a model defined in Swagger

        :param user_id: The user_id of this NewExperimentThreshold.  # noqa: E501
        :type user_id: str
        :param indicator: The indicator of this NewExperimentThreshold.  # noqa: E501
        :type indicator: str
        :param threshold: The threshold of this NewExperimentThreshold.  # noqa: E501
        :type threshold: int
        :param ticker: The ticker of this NewExperimentThreshold.  # noqa: E501
        :type ticker: str
        """
        self.swagger_types = {
            'user_id': str,
            'indicator': str,
            'threshold': int,
            'ticker': str
        }

        self.attribute_map = {
            'user_id': 'userID',
            'indicator': 'indicator',
            'threshold': 'threshold',
            'ticker': 'ticker'
        }

        self._user_id = user_id
        self._indicator = indicator
        self._threshold = threshold
        self._ticker = ticker

    @classmethod
    def from_dict(cls, dikt) -> 'NewExperimentThreshold':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The newExperimentThreshold of this NewExperimentThreshold.  # noqa: E501
        :rtype: NewExperimentThreshold
        """
        return util.deserialize_model(dikt, cls)

    @property
    def user_id(self) -> str:
        """Gets the user_id of this NewExperimentThreshold.


        :return: The user_id of this NewExperimentThreshold.
        :rtype: str
        """
        return self._user_id

    @user_id.setter
    def user_id(self, user_id: str):
        """Sets the user_id of this NewExperimentThreshold.


        :param user_id: The user_id of this NewExperimentThreshold.
        :type user_id: str
        """
        if user_id is None:
            raise ValueError("Invalid value for `user_id`, must not be `None`")  # noqa: E501

        self._user_id = user_id

    @property
    def indicator(self) -> str:
        """Gets the indicator of this NewExperimentThreshold.


        :return: The indicator of this NewExperimentThreshold.
        :rtype: str
        """
        return self._indicator

    @indicator.setter
    def indicator(self, indicator: str):
        """Sets the indicator of this NewExperimentThreshold.


        :param indicator: The indicator of this NewExperimentThreshold.
        :type indicator: str
        """
        if indicator is None:
            raise ValueError("Invalid value for `indicator`, must not be `None`")  # noqa: E501

        self._indicator = indicator

    @property
    def threshold(self) -> int:
        """Gets the threshold of this NewExperimentThreshold.


        :return: The threshold of this NewExperimentThreshold.
        :rtype: int
        """
        return self._threshold

    @threshold.setter
    def threshold(self, threshold: int):
        """Sets the threshold of this NewExperimentThreshold.


        :param threshold: The threshold of this NewExperimentThreshold.
        :type threshold: int
        """
        if threshold is None:
            raise ValueError("Invalid value for `threshold`, must not be `None`")  # noqa: E501

        self._threshold = threshold

    @property
    def ticker(self) -> str:
        """Gets the ticker of this NewExperimentThreshold.


        :return: The ticker of this NewExperimentThreshold.
        :rtype: str
        """
        return self._ticker

    @ticker.setter
    def ticker(self, ticker: str):
        """Sets the ticker of this NewExperimentThreshold.


        :param ticker: The ticker of this NewExperimentThreshold.
        :type ticker: str
        """
        if ticker is None:
            raise ValueError("Invalid value for `ticker`, must not be `None`")  # noqa: E501

        self._ticker = ticker
