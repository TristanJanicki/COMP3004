# native imports
import connexion
import six
import logging
import uuid
from datetime import datetime, timedelta
import dateutil.parser

# project imports
################################## GENERATED IMPORTS ########################################
from swagger_server.models.already_exists_response import AlreadyExistsResponse  # noqa: E501
from swagger_server.models.bad_input_response import BadInputResponse
from swagger_server.models.error_response import ErrorResponse  # noqa: E501
from swagger_server.models.existing_correlation_experiment import ExistingCorrelationExperiment  # noqa: E501
from swagger_server.models.existing_threshold_experiment import ExistingThresholdExperiment  # noqa: E501
from swagger_server.models.get_experiments_result import GetExperimentsResult  # noqa: E501
from swagger_server.models.new_correlation_post import NewCorrelationPost  # noqa: E501
from swagger_server.models.new_threshold_experiment import NewThresholdExperiment  # noqa: E501
from swagger_server.models.not_allowed_response import NotAllowedResponse  # noqa: E501
from swagger_server.models.ok_response import OkResponse  # noqa: E501
from swagger_server import util
################################## CUSTOM IMPORTS ###########################################
from swagger_server.infrastructure.db.mysql import mysql
from swagger_server.infrastructure.converters import experimentConverter as converter
from swagger_server.database_models.ThresholdExperiment import ThresholdExperiment
from swagger_server.database_models.CorrelationExperiment import CorrelationExperiment
from swagger_server.database_models.User import User

# external library imports
from sqlalchemy.sql import exists

# initializing loggers and sql manager
sqlManager = mysql.SqlManager()
logger = logging.getLogger()
logger.setLevel(logging.INFO)


def experiments_correlation_create(experiment=None):  # noqa: E501
    """experiments_correlation_create

    Add a new experiment to a users account # noqa: E501

    :param idToken: id token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    try:
        if experiment == None:
            print("experiment was None")
            return BadInputResponse("Experiment was None")

        userID = connexion.request.headers['user_id']

        user = sqlManager.session.query(User).filter_by(user_id=userID).one()
        usersExperiments = []
        if user.experiments != None:
            usersExperiments = user.experiments.split(",")

        experiment = connexion.request.json["experiment"]
        existingCopy = None
        try:
            existingCopy = sqlManager.session.query(CorrelationExperiment).filter_by(
                asset_1=experiment["asset_1"], asset_2=experiment["asset_2"]).one()
        except:
            pass
        # fill existing copy with a value from the db if there is one
        sqlManager.session.commit()

        # the experiment already exists, lets check if it needs to be updated (last updated needs to be older than 1 day)
        if existingCopy != None:
            last_updated_at = existingCopy.last_updated_at
            days_since_update = datetime.now() - last_updated_at

            if days_since_update.days > 1:
                existingCopy.status = "update_requested"
                existingCopy.update_requested_at = datetime.now()

            if existingCopy.experiment_id not in usersExperiments:
                usersExperiments.append(existingCopy.experiment_id)
            else:
                return AlreadyExistsResponse()
        else:  # The experiment doesn't exist, lets create it
            experiment_id = str(uuid.uuid4())
            dbExperiment = CorrelationExperiment(
                experiment_id=experiment_id, asset_1=experiment["asset_1"], asset_2=experiment["asset_2"], correlation=0, status="update_requested", update_requested_at=datetime.now(), last_updated_at=datetime.now())
            sqlManager.session.add(dbExperiment)
            usersExperiments.append(experiment_id)

        user.experiments = ','.join(usersExperiments)
        sqlManager.session.commit()
    except:
        sqlManager.session.rollback()
        return ErrorResponse()
    return OkResponse()


def experiments_correlation_update(experiment=None):  # noqa: E501
    """experiments_correlation_update

    Update a experiment # noqa: E501

    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    if connexion.request.is_json:
        experiment = ExistingCorrelationExperiment.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def experiments_threshold_create(experiment=None):  # noqa: E501
    """experiments_threshold_create

    Add a new experiment to a users account # noqa: E501

    :param idToken: id token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str
    :param experiment: 
    :type experiment: dict | bytes

    :rtype: OkResponse
    """
    try:
        if experiment == None:
            print("experiment was None")
            return OkResponse("Experiment was None")

        userID = connexion.request.headers['user_id']

        user = sqlManager.session.query(User).filter_by(user_id=userID).one()
        usersExperiments = []
        if user.experiments != None:
            usersExperiments = user.experiments.split(",")

        experiment = connexion.request.json["experiment"]
        existingCopy = None
        try:
            existingCopy = sqlManager.session.query(ThresholdExperiment).filter_by(
                ticker=experiment["ticker"], threshold=experiment["threshold"]).one()
        except:
            return ErrorResponse()
        # fill existing copy with a value from the db if there is one
        sqlManager.session.commit()

        # the experiment already exists, lets check if it needs to be updated (last updated needs to be older than 1 day)
        if existingCopy != None:
            last_updated_at = existingCopy.last_updated_at
            days_since_update = datetime.now() - last_updated_at

            if days_since_update.days > 1:
                existingCopy.status = "update_requested"
                existingCopy.update_requested_at = datetime.now()

            if existingCopy.experiment_id not in usersExperiments:
                usersExperiments.append(existingCopy.experiment_id)
            else:
                return AlreadyExistsResponse()
        else:  # The experiment doesn't exist, lets create it
            experiment_id = str(uuid.uuid4())
            dbExperiment = ThresholdExperiment(
                experiment_id=experiment_id, indicator=experiment["indicator"], threshold=experiment["threshold"], ticker=experiment["ticker"], status="update_requested", update_requested_at=datetime.now(), last_updated_at=datetime.now())
            sqlManager.session.add(dbExperiment)
            usersExperiments.append(experiment_id)

        user.experiments = ','.join(usersExperiments)
        sqlManager.session.commit()
    except:
        sqlManager.session.rollback()
        return ErrorResponse()

    return OkResponse()


def user_experiments_delete():  # noqa: E501
    """user_experiments_delete

    Delete a experiment from a users experiments list # noqa: E501

    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param experimentID: The database ID of the experiment
    :type experimentID: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str

    :rtype: OkResponse
    """
    userID = connexion.request.headers['user_id']
    experimentToDelete = connexion.request.headers['experiment_id']
    user = sqlManager.session.query(User).filter_by(user_id=userID).one()
    usersExperiments = []
    if user.experiments != None:
        usersExperiments = user.experiments.split(",")

    newUserExperiments = []

    for ex in usersExperiments:
        if ex != experimentToDelete:
            newUserExperiments.append(ex)

    usersExperiments = ",".join(newUserExperiments)

    user.experiments = usersExperiments
    sqlManager.session.merge(user)
    sqlManager.session.commit()

    return OkResponse()


def user_experiments_get_all():  # noqa: E501
    """user_experiments_get_all

    Get all experiments associated/owned by a user # noqa: E501

    :param idToken: access token obtained from AWS Cognito
    :type idToken: str
    :param user_id: the users ID to associate the experiment with
    :type user_id: str

    :rtype: GetExperimentsResult
    """

    userID = connexion.request.headers['user_id']
    user = sqlManager.session.query(User).filter_by(user_id=userID).one()

    usersCorrelations = []
    usersThresholds = []
    usersExperiments = user.experiments.split(",")
    print(usersExperiments)

    for ex in usersExperiments:
        stmt = exists().where(ThresholdExperiment.experiment_id==ex)
        for thExp in sqlManager.session.query(ThresholdExperiment).filter(ThresholdExperiment.experiment_id==ex):
            converted = converter.convertDbThresholdExperimentToSwaggerExperiment(thExp)
            usersThresholds.append(converted)

        stmt = exists().where(CorrelationExperiment.experiment_id==ex)
        for corrExp in sqlManager.session.query(CorrelationExperiment).filter(CorrelationExperiment.experiment_id==ex):
            converted = converter.convertDbCorrelationExperimentToSwaggerExperiment(corrExp)
            usersCorrelations.append(converted)


    return GetExperimentsResult(correlations=usersCorrelations, thresholds=usersThresholds)

