package handlers

import (
	"errors"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/go-openapi/runtime/middleware"
	"github.com/sirupsen/logrus"

	"github.com/COMP3004/UserAccounts/infrastructure/db/mysql"
	"github.com/COMP3004/UserAccounts/pkg/aws/cognito"
	genModels "github.com/COMP3004/UserAccounts/pkg/gen/models"
	"github.com/COMP3004/UserAccounts/pkg/gen/restapi/operations"
	dbModels "github.com/COMP3004/UserAccounts/pkg/models"
)

type UserAccountsHandler struct {
	cognitoHandler *cognito.AwsCognitoHandler
	dbManager      *mysql.SqlDbManager
	log            *logrus.Entry
}

func NewUserAccountsHandler(db *mysql.SqlDbManager, c *cognito.AwsCognitoHandler) (*UserAccountsHandler, error) {
	handler := &UserAccountsHandler{
		log:            logrus.StandardLogger().WithField("type", "user_accounts_handler"),
		dbManager:      db,
		cognitoHandler: c,
	}
	handler.dbManager.Db = handler.dbManager.Db.AutoMigrate(&dbModels.UserAccount{})

	if handler.dbManager.Db.Error != nil {
		handler.log.WithError(handler.dbManager.Db.Error).Warn("Failed to initialize user accounts handler")
		return nil, handler.dbManager.Db.Error
	}

	return handler, nil
}

// CreateAccount
func (m *UserAccountsHandler) CreateUserAccount(params operations.CreateUserAccountParams) middleware.Responder {
	log := m.log.WithFields(logrus.Fields{
		"method": "CreateUserAccount",
	})
	// TODO: verify with license and billing service that this agency is allowed another user and that this user is in a role that allows for creating other users (admins only)
	// Fetch call user account and check if they are an admin
	var callerUserProfileDb dbModels.UserAccount
	queryResult := m.dbManager.Db.Where("proelium_user_id = ?",
		params.UserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewCreateUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Caller user account not found. Operation not authorized")
		return operations.NewCreateUserAccountUnauthorized()
	}

	// Check if a user with same email pre-exists
	userEmail := params.UserAccount.Profile.Email.String()
	queryResult = m.dbManager.Db.Where("email = ?", userEmail).First(&dbModels.UserAccount{})
	if !queryResult.RecordNotFound() {
		log.Info("User account already exists")
		return operations.NewCreateUserAccountConflict().WithPayload(&genModels.AlreadyExistsResponse{
			Message: aws.String("User with same email address already exists"),
		})
	}

	// Create cognito profile
	userId, err := m.cognitoHandler.RegisterUserWithCognito(&userEmail)
	if err != nil {
		log.WithError(err).Warn("User account creation failed")
		if strings.Contains(err.Error(), "UsernameExistsException") == true {
			return operations.NewCreateUserAccountConflict().WithPayload(&genModels.AlreadyExistsResponse{
				Message: aws.String("User with same email address already exists"),
			})
		}
		return operations.NewCreateUserAccountInternalServerError()
	}

	newUserProfileDb, err := dbModels.ConvertToDbModelUserAccount(*userId, params.UserAccount)
	if err != nil {
		log.WithError(err).Warn("Bad input")
		return operations.NewCreateUserAccountBadRequest()
	}

	// Start db transaction
	tx := m.dbManager.Db.Begin()
	if err := tx.Create(newUserProfileDb).Error; err != nil {
		log.WithError(err).Warn("Failed to create new user account")
		tx.Rollback()
		m.cognitoHandler.DeleteUserFromCognito(&userEmail)
		return operations.NewCreateUserAccountInternalServerError()
	}

	// Commit transaction
	if err := tx.Commit().Error; err != nil {
		log.WithError(err).Warn("Failed to commit to db")
		tx.Rollback()
		m.cognitoHandler.DeleteUserFromCognito(&userEmail)
		return operations.NewCreateUserAccountInternalServerError()
	}

	response := operations.NewCreateUserAccountCreated()
	response.Payload = &genModels.PostOkResponse{
		ID: userId,
	}

	return response
}

func (m *UserAccountsHandler) GetUserAccount(params operations.GetUserAccountParams) middleware.Responder {
	log := m.log.WithFields(logrus.Fields{
		"method": "GetUserAccount",
	})

	// Fetch call user account and check if they are an admin
	var callerUserProfileDb = &dbModels.UserAccount{}
	var targetUserProfileDb = &dbModels.UserAccount{}

	queryResult := m.dbManager.Db.Where("user_id = ?",
		params.UserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewGetUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Caller user not found. Operation not authorized")
		return operations.NewGetUserAccountUnauthorized()
	}

	var userProfile *genModels.UserAccountResult
	if params.UserID == params.UserID {
		var err error
		userProfile, err = m.convertUserAccount(callerUserProfileDb)
		if err != nil {
			log.WithError(err).Warn("Error converting caller models")
			return operations.NewGetUserAccountInternalServerError()
		}
	} else {
		queryResult := m.dbManager.Db.Where("user_id = ?",
			params.UserID).First(&targetUserProfileDb)
		if queryResult.RecordNotFound() {
			log.Warn("User not found")
			return operations.NewGetUserAccountNotFound()
		}

		var err error
		userProfile, err = m.convertUserAccount(targetUserProfileDb)
		if err != nil {
			log.WithError(err).Warn("Error converting target models")
			return operations.NewGetUserAccountInternalServerError()
		}
	}

	response := operations.NewGetUserAccountOK()
	response.Payload = userProfile

	return response
}

func (m *UserAccountsHandler) GetAllUserAccounts(params operations.GetAllUserAccountsParams) middleware.Responder {
	log := m.log.WithFields(logrus.Fields{
		"method": "GetAllUserAccounts",
	})

	// Retrieve user profiles
	var userProfilesDb []*dbModels.UserAccount

	queryResult := m.dbManager.Db.
		Order("updated_at desc").Select("*").Find(&userProfilesDb)
	if queryResult.RecordNotFound() {
		log.Info("No users found")
		return operations.NewGetAllUserAccountsNotFound()
	}
	if queryResult.Error != nil {
		log.WithError(queryResult.Error).Warn("Failed to get users")
		return operations.NewGetAllUserAccountsInternalServerError()
	}

	// Convert users
	var userProfiles []*genModels.UserAccountResult
	for _, userProfileDb := range userProfilesDb {
		// Only include stats if caller user is an admin or the owner of the profile

		tmp, err := m.convertUserAccount(userProfileDb)
		if err != nil {
			log.WithError(err).Warn("Failed to convert models")
			return operations.NewGetAllUserAccountsInternalServerError()
		}
		userProfiles = append(userProfiles, tmp)
	}

	response := operations.NewGetAllUserAccountsOK()
	response.Payload = &genModels.UserAccountResults{Users: userProfiles}

	return response
}

func (m *UserAccountsHandler) UpdateUserAccount(params operations.UpdateUserAccountParams) middleware.Responder {
	log := m.log.WithFields(logrus.Fields{
		"method": "UpdateUserAccount",
	})

	// Fetch call user account and check if they are an admin
	var callerUserProfileDb dbModels.UserAccount

	queryResult := m.dbManager.Db.Where("user_id = ?",
		params.UserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewUpdateUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Call user not found. Operation not authorized")
		return operations.NewUpdateUserAccountUnauthorized()
	}

	var currUserProfileDb *dbModels.UserAccount
	if params.CallerUserID == params.UserID {
		currUserProfileDb = &callerUserProfileDb
	} else {
		return operations.NewUpdateUserAccountUnauthorized() // users aren't allowed to update other users' accounts.
	}

	newUserProfileDb, err := dbModels.ConvertToDbModelExistingUserAccount(params.UserID, params.UserAccount)
	if err != nil {
		log.WithError(err).Warn("Failed to update user account")
		return operations.NewUpdateUserAccountInternalServerError()
	}

	profileUpdates := m.getUserProfileUpdates(currUserProfileDb, newUserProfileDb)

	// Start db transaction
	tx := m.dbManager.Db.Begin()
	if len(*profileUpdates) > 0 {
		if err := tx.Model(*currUserProfileDb).Updates(*profileUpdates).Error; err != nil {
			log.WithError(err).Warn("Failed to update user profile")
			tx.Rollback()
			return operations.NewUpdateUserAccountInternalServerError()
		}
	}

	// Commit transaction
	if len(*profileUpdates) > 0 {
		if err := tx.Commit().Error; err != nil {
			log.WithError(err).Warn("Failed to commit schedule to db")
			tx.Rollback()
			return operations.NewUpdateUserAccountInternalServerError()
		}
	}

	return operations.NewUpdateUserAccountOK()
}

func (m *UserAccountsHandler) DeleteUserAccount(params operations.DeleteUserAccountParams) middleware.Responder {
	log := m.log.WithFields(logrus.Fields{
		"method": "DeleteUserAccount",
	})

	log.Info("DeleteUserAccount")

	if params.UserID != params.CallerUserID {
		return operations.NewDeleteUserAccountUnauthorized().WithPayload(&genModels.NotAllowedResponse{
			Message: aws.String("you're only allowed to delete your own account"),
		})
	}

	userAccount := &dbModels.UserAccount{}

	queryResult := m.dbManager.Db.Where("user_id = ?", params.UserID).First(userAccount)

	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching user account to delete")
		return operations.NewUpdateUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Call user not found. Operation not authorized")
		return operations.NewUpdateUserAccountUnauthorized()
	}

	m.cognitoHandler.DeleteUserFromCognito(userAccount.Email)

	queryResult = m.dbManager.Db.Delete(userAccount)

	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error deleting user account from database")
		return operations.NewDeleteUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Couldn't find account to be deleted")
		return operations.NewDeleteUserAccountNotFound()
	}

	return operations.NewDeleteUserAccountOK()
}

func (m *UserAccountsHandler) convertUserAccount(profile *dbModels.UserAccount) (*genModels.UserAccountResult, error) {
	log := m.log.WithFields(logrus.Fields{
		"method": "convertUserAccount",
	})
	var userAccount *genModels.UserAccount
	//	var assignedClients []*genModels.UserAssignedClient

	queryResult := m.dbManager.Db.Where("user_id = ?",
		profile.UserId).First(&userAccount)
	if !queryResult.RecordNotFound() {
		userAccount = dbModels.ConvertToSwaggerModelUserAccount(profile)
	} else if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		return nil, queryResult.Error
	} else {
		log.WithError(queryResult.Error).Info("error converting user account")
		return nil, errors.New("error converting user account")
	}

	return &genModels.UserAccountResult{
		ID:          &profile.UserId,
		UserAccount: userAccount,
	}, nil
}

func (m *UserAccountsHandler) getUserProfileUpdates(currModel *dbModels.UserAccount, newModel *dbModels.UserAccount) *map[string]interface{} {
	var result = make(map[string]interface{})

	// Profile
	if newModel.FirstName != nil && *currModel.FirstName != *newModel.FirstName {
		result["first_name"] = newModel.FirstName
	}
	if newModel.LastName != nil && *currModel.LastName != *newModel.LastName {
		result["last_name"] = newModel.LastName
	}
	if newModel.Email != nil && *currModel.Email != *newModel.Email {
		result["email"] = newModel.Email
	}
	if newModel.NickName != nil && (currModel.NickName == nil || *currModel.NickName != *newModel.NickName) {
		result["nickname"] = newModel.NickName
	}
	if newModel.Title != nil && (currModel.Title == nil || *currModel.Title != *newModel.Title) {
		result["title"] = newModel.Title
	}
	if newModel.Country != nil && (currModel.Country == nil || *currModel.Country != *newModel.Country) {
		result["country"] = newModel.Country
	}
	if newModel.CountryCode != nil && (currModel.CountryCode == nil || *currModel.CountryCode != *newModel.CountryCode) {
		result["country_code"] = newModel.CountryCode
	}
	if newModel.PhoneNumber != nil && (currModel.PhoneNumber == nil || *currModel.PhoneNumber != *newModel.PhoneNumber) {
		result["phone_number"] = newModel.PhoneNumber
	}

	return &result
}
