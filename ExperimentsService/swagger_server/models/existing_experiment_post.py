# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from swagger_server.models.base_model_ import Model
from swagger_server import util


class ExistingExperimentPost(Model):
    """NOTE: This class is auto generated by the swagger code generator program.

    Do not edit the class manually.
    """

    def __init__(self, experiments: List[object]=None):  # noqa: E501
        """ExistingExperimentPost - a model defined in Swagger

        :param experiments: The experiments of this ExistingExperimentPost.  # noqa: E501
        :type experiments: List[object]
        """
        self.swagger_types = {
            'experiments': List[object]
        }

        self.attribute_map = {
            'experiments': 'experiments'
        }

        self._experiments = experiments

    @classmethod
    def from_dict(cls, dikt) -> 'ExistingExperimentPost':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The existingExperimentPost of this ExistingExperimentPost.  # noqa: E501
        :rtype: ExistingExperimentPost
        """
        return util.deserialize_model(dikt, cls)

    @property
    def experiments(self) -> List[object]:
        """Gets the experiments of this ExistingExperimentPost.


        :return: The experiments of this ExistingExperimentPost.
        :rtype: List[object]
        """
        return self._experiments

    @experiments.setter
    def experiments(self, experiments: List[object]):
        """Sets the experiments of this ExistingExperimentPost.


        :param experiments: The experiments of this ExistingExperimentPost.
        :type experiments: List[object]
        """
        if experiments is None:
            raise ValueError("Invalid value for `experiments`, must not be `None`")  # noqa: E501

        self._experiments = experiments
