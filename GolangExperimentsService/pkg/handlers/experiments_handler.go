package handlers

import (
	"strings"

	uuid "github.com/satori/go.uuid"
	log "github.com/sirupsen/logrus"

	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/infrastructure/db/mysql"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/gen/restapi/operations"
	dbModels "github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/models"
	genModels "github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/gen/models"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/utils"
	"github.com/go-openapi/runtime/middleware"
)

//ExperimentsHandler Object used for storing things related to processing experiments.
type ExperimentsHandler struct {
	sqlDbManager *mysql.SqlDbManager
	log          *log.Logger
}

//NewExperimentsHandler Create a new instance of the experiments handler.
func NewExperimentsHandler(dbManager *mysql.SqlDbManager) *ExperimentsHandler {
	return &ExperimentsHandler{
		sqlDbManager: dbManager,
		log:          log.StandardLogger(),
	}
}

func (h ExperimentsHandler) GetUser(userID string) (error, dbModels.UserAccount) {
	var user dbModels.UserAccount{}

	log := h.log.WithField("method", "GetUser")

	queryResult := h.sqlDbManager.Db.Where("user_id = ?", params.UserID).First(&user)

	if queryResult.Error != nil && !queryResult.RecordNotFound() {
		log.WithError(queryResult.Error).Warn("Failed to query for the account of the calling user")
		return queryResult.Error, nil
	}

	if queryResult.RecordNotFound() {
		log.WithError(queryResult.Error).Warn("Couldn't find user: ", params.UserID)
		return queryResult.Error, nil
	}
	return nil, user
}

func (h ExperimentsHandler) GetUserExperiments(params operations.GetUserExperimentsParams) middleware.Responder {
	log := h.log.WithField("method", "GetUserExperiments")

	user, err := h.GetUser(params.UserID, log)
	
	if err != nil {
		log.WithError(err).Warn("Failed to get user")
		return operations.NewGetUserExperimentsInternalServerError()
	}

	var thresholds []*dbModels.ThresholdExperiment
	var correlations []*dbModels.CorrelationExperiments

	for _, experiment_id := range user.Experiments {
		// Search for it in ThresholdExperiments
		var threshold dbModels.ThresholdExperiment{}
		tx := h.dbManager.Db.Begin()

		queryResult := tx.Where("experiment_id = ?", experiment_id).First(&threshold)
		if queryResult.Error != nil && !queryResult.RecordNotFound(){
			log.WithError(queryResult.Error).Warn("Failed to query to see if a user experiment was a ThresholdExperiment")
			return operations.NewGetUserExperimentsInternalServerError()
		}

		if !queryResult.RecordNotFound() {
			thresholds = append(thresholds, dbModels.ConvertDbThresholdToSwaggerThreshold(threshold))
			continue // if this experiment is a existant threshold then we don't need to do any more work this iteration
		}
		

		var correlation dbModels.CorrelationExperiment
		queryResult := tx.Where("experiment_id = ?", experiment_id).First(&correlation)

		if queryResult.Error != nil && !queryResult.RecordNotFound() {
			log.WithError(queryResult.Error).Warn("Failed to query to see if experiment was correlation experiment")
			return operations.NewGetUserExperimentsInternalServerError()
		}

		if queryResult.RecordNotFound() {
			log.Warn("user ", params.UserID, " is subscribed to a non existent experiment")
			h.DeleteUserExperiment(genModels.DeleteUserExperimentParams{
				XRequestID: params.XRequestID,
				ExperimentID: val,
				IDToken: params.IDToken,
				UserID: params.UserID,
			})
			continue
		}

		correlations = append(correlations, dbModels.ConvertDbCorrelationToSwaggerCorrelation(correlation))

		// If its not present search for it in CorrelationExperiments
	}

	return operations.NewGetUserExperimentsOK().WithPayload(&genModels.GetExperimentsResult{
		Correlations: correlations,
		Thresholds: thresholds,
	})
}

func (h ExperimentsHandler) DeleteUserExperiment(params operations.DeleteUserExperimentParams) middleware.Responder {
	log := h.log.WithField("method", "DeleteUserExperiment")

	user, err := h.GetUser(params.UserID, log)

	oldExperiments = strings.Split(",", user.Experiments)
	newExperiments = []string

	for _, e := range oldExperiments {
		if !e.ExperimentID == params.ExperimentID {
			newExperiments = append(e, newExperiments)
		}
	}

	user.Experiments = strings.Join(newExperiments, ",")

	tx := h.dbManager.Db.Begin()

	err := tx.Save(user).Commit()

	if err != nil {
		tx.Rollback()
		log.WithError(err).Warn("Failed to update user db entry to remove experiment")
		return operations.NewDeleteUserExperimentInternalServerError()
	}

	return operations.NewDeleteUserExperimentOK()
}

func (h ExperimentsHandler) CreateCorrelationExperiment(params operations.CreateCorrelationExperimentParams) middleware.Responder {
	log := h.log.WithField("method", "CreateCorrelationExperiment")

	user, err := h.GetUser(params.UserID, log)

	if err != nil {
		log.WithError(err).Warn("Failed to get user")
		return operations.NewCreateThresholdExperimentInternalServerError()
	}

	experiments := strings.Split(",", user.Experiments)

	if len(experiments) >= utils.GetAccountTypeOperationResitrctions(user.AccountType, utils.ADD_EXPERIMENT){
		log.Info("user ", params.UserID, " has reach their experiment maximum")
		return operations.NewCreateThresholdExperimentConflict() // TODO: should create a specific response code for maxed out accounts
	}

	var experiment dbModels.CorrelationExperiment

	queryResult := h.sqlDbManager.Db.Where("asset_1 = ? and asset_1 = ?",
		params.Experiment.ASset_2, params.Experiment.Asset_1).First(&experiment)

	if queryResult.Error != nil && !queryResult.RecordNotFound() {
		log.WithError(queryResult.Error).Warn("Error querying for existing experiment")
		return operations.NewCreateCorrelationExperimentInternalServerError()
	}

	tx := h.dbManager.Db.Being()
	if queryResult.RecordNotFound() {
		// experiment doesn't exist so lets create it
		experimentID := uuid.NewV4().String()

		experiment = dbModels.CorrelationExperiment{
			Experiment_id: experimentID,
			Asset1:       params.Asset_1,
			Asset2:       params.Asset_2,
			AssetCombo:         &params.AssetCombo,
		}

		queryResult := tx.Create(&experiment).Commit()

		if queryResult.Error != nil {
			tx.Rollback()
			log.WithError(queryResult.Error).Warn("Failed to create correlation experiment")
			return operations.NewCreateCorrelationExperimentInternalServerError()
		}
	}

	user.Experiments = user.Experiments + "," + experiment.ExperimentID

	queryResult = tx.Save(user).Commit()

	if queryResult.Error != nil && !queryResult.RecordNotFound() {
		tx.Rollback()
		log.WithError(queryResult.Error).Warn("failed to subscribe user to correlation experiment")
		return operations.NewCreateCorrelationExperimentInternalServerError()
	}

	return operations.NewCreateCorrelationExperimentOK()
}

// CreateThresholdExperiment handles storing in the database then computing a threshold experiment.
func (h ExperimentsHandler) CreateThresholdExperiment(params operations.CreateThresholdExperimentParams) middleware.Responder {
	log := h.log.WithField("method", "CreateThresholdExperiment")

	user, err := h.GetUser(params.UserID, log)

	if err != nil {
		log.WithError(err).Warn("Failed to get user")
		return operations.NewCreateThresholdExperimentInternalServerError()
	}

	experiments := strings.Split(",", user.Experiments)

	// is the user able to add another experiment
	if len(experiments) >= utils.GetAccountTypeOperationResitrctions(user.AccountType, utils.ADD_EXPERIMENT) {
		log.Info("user ", *user.UserId, " has reached their experiment maximum")
		return operations.NewCreateThresholdExperimentConflict() // TODO: should create a specific response code for maxed out accounts
	}

	var experiment dbModels.ThresholdExperiment

	queryResult = h.sqlDbManager.Db.Where("ticker = ? and indicator = ? and threshold = ?",
		params.Experiment.Ticker, params.Experiment.Indicator, params.Experiment.Threshold).First(&experiment)

	if queryResult.Error != nil && !queryResult.RecordNotFound() {
		log.WithError(queryResult.Error).Warn("Error querying for existing experiment")
		return operations.NewCreateThresholdExperimentInternalServerError()
	}

	if queryResult.RecordNotFound() {
		// experiment doesn't exist so lets create it

		experimentID := uuid.NewV4().String()

		experiment = dbModels.ThresholdExperiment{
			Experiment_id: experimentID,
			Indicator:     params.Experiment.Indicator,
			Threshold:     params.Experiment.Threshold,
		}

		tx := h.sqlDbManager.Db.Begin()

		err := tx.Create(&experiment).Commit()

		if err != nil {
			tx.Rollback()
			log.WithError(err).Warn("failed to store new experiment in database")
			return operations.NewCreateThresholdExperimentInternalServerError()
		}
	}

	user.Experiments = user.Experiments + "," + experiment.ExperimentID

	tx = h.sqlDbManager.Db.Begin()

	err := tx.Save(user).Commit()

	if err != nil {
		tx.Rollback()
		log.WithError(err).Warn("Failed to save user with new experiment")
		return operations.NewCreateThresholdExperimentInternalServerError()
	}

	return operations.NewCreateCorrelationExperimentOK()
}
