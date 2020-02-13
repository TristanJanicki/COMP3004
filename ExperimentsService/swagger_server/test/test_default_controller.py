# coding: utf-8

from __future__ import absolute_import

from flask import json
from six import BytesIO

from swagger_server.models.already_exists_response import AlreadyExistsResponse  # noqa: E501
from swagger_server.models.error_response import ErrorResponse  # noqa: E501
from swagger_server.models.existing_experiment_post import ExistingExperimentPost  # noqa: E501
from swagger_server.models.get_experiment_result import GetExperimentResult  # noqa: E501
from swagger_server.models.new_experiment_post import NewExperimentPost  # noqa: E501
from swagger_server.models.not_allowed_response import NotAllowedResponse  # noqa: E501
from swagger_server.models.ok_response import OkResponse  # noqa: E501
from swagger_server.test import BaseTestCase


class TestDefaultController(BaseTestCase):
    """DefaultController integration test stubs"""

    def test_create_experiment(self):
        """Test case for create_experiment

        
        """
        experiment = NewExperimentPost()
        headers = [('X_Request_ID', 'X_Request_ID_example'),
                   ('access_token', 'access_token_example')]
        response = self.client.open(
            '/v1/experiments/{user_id}'.format(user_id='user_id_example'),
            method='POST',
            data=json.dumps(experiment),
            headers=headers,
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_experiment(self):
        """Test case for delete_experiment

        
        """
        headers = [('X_Request_ID', 'X_Request_ID_example'),
                   ('access_token', 'access_token_example'),
                   ('experimentID', 'experimentID_example')]
        response = self.client.open(
            '/v1/experiments/{user_id}'.format(user_id='user_id_example'),
            method='DELETE',
            headers=headers,
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_all_experiments(self):
        """Test case for get_all_experiments

        
        """
        response = self.client.open(
            '/v1/experiments/{user_id}',
            method='GET',
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_update_experiment(self):
        """Test case for update_experiment

        
        """
        experiment = ExistingExperimentPost()
        headers = [('X_Request_ID', 'X_Request_ID_example'),
                   ('access_token', 'access_token_example')]
        response = self.client.open(
            '/v1/experiments/{user_id}'.format(user_id='user_id_example'),
            method='PUT',
            data=json.dumps(experiment),
            headers=headers,
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    import unittest
    unittest.main()
