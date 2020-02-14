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


def experiments_create(X_Request_ID, idToken, user_id, experiment=None):  # noqa: E501
    """experiments_create

    Add a new experiment to a users account # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID:
    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    if connexion.request.is_json:
        experiment = NewExperimentPost.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def experiments_delete(X_Request_ID, idToken, experimentID, user_id):  # noqa: E501
    """experiments_delete

    Delete a experiment # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param experimentID: The database ID of the experiment
    :type experimentID: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str

    :rtype: OkResponse
    """
    return 'do some magic!'


def experiments_get_all(X_Request_ID, idToken, user_id):  # noqa: E501
    """experiments_get_all

    Get all experiments associated/owned by a user # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str

    :rtype: GetExperimentResult
    """
    return 'do some magic!'


def experiments_update(X_Request_ID, idToken, user_id, experiment=None):  # noqa: E501
    """experiments_update

    Update a experiment # noqa: E501

    :param X_Request_ID: Request id
    :type X_Request_ID: 
    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    if connexion.request.is_json:
        experiment = ExistingExperimentPost.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
