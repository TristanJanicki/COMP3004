import connexion
import six
import logging

################################## GENERATED IMPORTS ########################################
from swagger_server.models.already_exists_response import AlreadyExistsResponse  # noqa: E501
from swagger_server.models.error_response import ErrorResponse  # noqa: E501
from swagger_server.models.existing_experiment_post import ExistingExperimentPost  # noqa: E501
from swagger_server.models.get_experiment_result import GetExperimentResult  # noqa: E501
from swagger_server.models.new_experiment_post import NewExperimentPost  # noqa: E501
from swagger_server.models.not_allowed_response import NotAllowedResponse  # noqa: E501
from swagger_server.models.ok_response import OkResponse  # noqa: E501
from swagger_server import util
################################## CUSTOM IMPORTS ###########################################
from swagger_server.infrastructure.db.mysql import mysql
from swagger_server.infrastructure.converters import experimentConverter as converter
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.User import User

sqlManager = mysql.SqlManager()
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def experiments_create():  # noqa: E501
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

    userID = connexion.request.headers['user_id']

    user = sqlManager.session.query(User).filter_by(user_id=userID).one()

    for experiment in connexion.request.json["experiments"]:
        print(experiment)
        existingCopy = None
        if "correlation" in experiment:
            existingCopy = sqlManager.session.query(CorrelationExperiment).filter_by(asset_1=experiment["asset_1"], asset_2=experiment["asset_2"]).one()
        else:
            existingCopy = sqlManager.session.query(ThresholdExperiment).filter_by(ticker=experiment["ticker"], indicator=experiment["indicator"], threshold=experiment["threshold"]).one()
        sqlManager.session.commit()
        if existingCopy != None:
            if existingCopy.status == "up_to_date":
                # TODO: add this experiment to the user's list of experiments
                return OkResponse()

        dbExperiment = ThresholdExperiment.ThresholdExperiment(userID, experiment["indicator"], experiment["threshold"], experiment["ticker"])
        sqlManager.session.add(dbExperiment)
        sqlManager.session.commit()

    
    return OkResponse()


def experiments_delete():  # noqa: E501
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


def experiments_get_all():  # noqa: E501
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

    thresholdExperiment=sqlManager.db.Table(
        'threshold_experiments', sqlManager.metadata, autoload = True, autoload_with = sqlManager.engine)
    correlationExperiment=sqlManager.db.Table(
        'correlation_experiments', sqlManager.metadata, autoload = True, autoload_with = sqlManager.engine)

    experiments=[]

   # idToken = connexion.request.headers['idToken']
    resultset=sqlManager.selectAll([correlationExperiment])

    for result in resultset:
        if result["correlation"] != None:
            # its a correlation experiment
            experiments.append(
                converter.convertDbCorrelationExperimentToSwaggerExperiment(result))

    resultset=sqlManager.selectAll([thresholdExperiment])
    for result in resultset:
        # its a threshold experiment
        experiments.append(
            converter.convertDbThresholdExperimentToSwaggerExperiment(result))

    return GetExperimentResult(experiments=experiments)

def experiments_update():  # noqa: E501
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
