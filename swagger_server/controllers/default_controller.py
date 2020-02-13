import connexion
import six

from swagger_server.models.already_exists_response import AlreadyExistsResponse  # noqa: E501
from swagger_server.models.error_response import ErrorResponse  # noqa: E501
from swagger_server.models.existing_experiment_post import ExistingExperimentPost  # noqa: E501
from swagger_server.models.get_experiment_result import GetExperimentResult  # noqa: E501
from swagger_server.models.new_experiment_post import NewExperimentPost  # noqa: E501
from swagger_server.models.not_allowed_response import NotAllowedResponse  # noqa: E501
from swagger_server.models.ok_response import OkResponse  # noqa: E501
from swagger_server import util


def create_experiment(X_Request_ID, access_token, user_id, experiment=None):  # noqa: E501
    """create_experiment

    Add a new experiment to a users account # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param access_token: access token obtained from AWS Cognito
    :type access_token: str
    :param user_id: 
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    if connexion.request.is_json:
        experiment = NewExperimentPost.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_experiment(X_Request_ID, access_token, experimentID, user_id):  # noqa: E501
    """delete_experiment

    Delete a experiment # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param access_token: access token obtained from AWS Cognito
    :type access_token: str
    :param experimentID: The database ID of the experiment
    :type experimentID: str
    :param user_id: 
    :type user_id: str

    :rtype: OkResponse
    """
    return 'do some magic!'


def get_all_experiments():  # noqa: E501
    """get_all_experiments

    Get all experiments associated/owned by a user # noqa: E501


    :rtype: GetExperimentResult
    """
    return 'do some magic!'


def update_experiment(X_Request_ID, access_token, user_id, experiment=None):  # noqa: E501
    """update_experiment

    Update a experiment # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param access_token: access token obtained from AWS Cognito
    :type access_token: str
    :param user_id: 
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    if connexion.request.is_json:
        experiment = ExistingExperimentPost.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
