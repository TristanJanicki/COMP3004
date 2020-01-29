package handlers

import (
	"errors"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/go-openapi/runtime/middleware"
	"github.com/sirupsen/logrus"

	"github.com/COMP3004/UserAccounts/infrastructure/db/mysql"
	"github.com/COMP3004/UserAccounts/infrastructure/utils"
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
	handler.dbManager.Db.Model(&dbModels.EmbeddedUserGovernanceProfile{}).AddForeignKey("proelium_user_id",
		"user_accounts(proelium_user_id)", "CASCADE", "RESTRICT")

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
	queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		params.ProeliumCustomerID,
		params.ProeliumUserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewCreateUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Caller user account not found. Operation not authorized")
		return operations.NewCreateUserAccountUnauthorized()
	}
	// Check if caller is admin or has the permission to perform this operation
	if !*callerUserProfileDb.IsAdmin {
		log.Warn("Non admin user tried to create a user account")
		return operations.NewCreateUserAccountUnauthorized()
	}

	// Fetch customer account and check for domain
	var customerProfileDb dbModels.CustomerAccount
	queryResult = m.dbManager.Db.Where("proelium_customer_id = ?", params.ProeliumCustomerID).First(&customerProfileDb)
	if queryResult.RecordNotFound() {
		log.Warn("Customer not found. Operation not authorized")
		return operations.NewCreateUserAccountUnauthorized()
	}
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		return operations.NewCreateUserAccountUnauthorized()
	}

	// Check that user email domain is a match with client domain
	if !utils.CompareDomain(params.UserAccount.Profile.Email.String(), *customerProfileDb.Domain) {
		log.Warn("User email domain does not match customer domain")
		return operations.NewCreateUserAccountBadRequest().WithPayload(&genModels.BadInputResponse{
			Message: aws.String("User email domain does not match customer domain"),
		})
	}

	// Check if a user with same email pre-exists
	userEmail := params.UserAccount.Profile.Email.String()
	queryResult = m.dbManager.Db.Where("proelium_customer_id = ? AND email = ?",
		params.ProeliumCustomerID,
		userEmail).First(&dbModels.UserAccount{})
	if !queryResult.RecordNotFound() {
		log.Info("User account already exists")
		return operations.NewCreateUserAccountConflict().WithPayload(&genModels.AlreadyExistsResponse{
			Message: aws.String("User with same email address already exists"),
		})
	}

	// Create cognito profile
	userId, err := m.cognitoHandler.RegisterUserWithCognito(&params.ProeliumCustomerID, &userEmail)
	if err != nil {
		log.WithError(err).Warn("User account creation failed")
		if strings.Contains(err.Error(), "UsernameExistsException") == true {
			return operations.NewCreateUserAccountConflict().WithPayload(&genModels.AlreadyExistsResponse{
				Message: aws.String("User with same email address already exists"),
			})
		}
		return operations.NewCreateUserAccountInternalServerError()
	}

	newUserProfileDb, governanceProfileDb, err := dbModels.ConvertToDbModelUserAccount(params.ProeliumCustomerID, *userId, params.UserAccount)
	if err != nil {
		log.WithError(err).Warn("Bad input")
		return operations.NewCreateUserAccountBadRequest()
	}

	// Start db transaction
	tx := m.dbManager.Db.Begin()
	if err := tx.Create(newUserProfileDb).Error; err != nil {
		log.WithError(err).Warn("Failed to create new user account")
		tx.Rollback()
		m.cognitoHandler.DeleteUserFromCognito(&params.ProeliumCustomerID, &userEmail)
		return operations.NewCreateUserAccountInternalServerError()
	}
	if err := tx.Create(governanceProfileDb).Error; err != nil {
		log.WithError(err).Warn("Failed to create new user governance profile")
		tx.Rollback()
		m.cognitoHandler.DeleteUserFromCognito(&params.ProeliumCustomerID, &userEmail)
		return operations.NewCreateUserAccountInternalServerError()
	}
	// Commit transaction
	if err := tx.Commit().Error; err != nil {
		log.WithError(err).Warn("Failed to commit to db")
		tx.Rollback()
		m.cognitoHandler.DeleteUserFromCognito(&params.ProeliumCustomerID, &userEmail)
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

	queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		params.ProeliumCustomerID,
		params.ProeliumUserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewGetUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Caller user not found. Operation not authorized")
		return operations.NewGetUserAccountUnauthorized()
	}
	// Check if caller is admin or is the owner of the profile
	if !*callerUserProfileDb.IsAdmin && (params.ProeliumUserID != params.UserID) {
		log.Warn("Non admin user or non owner tried to fetch profile")
		return operations.NewGetUserAccountUnauthorized()
	}

	var userProfile *genModels.UserAccountResult
	if params.ProeliumUserID == params.UserID {
		var err error
		userProfile, err = m.convertUserAccount(callerUserProfileDb, true)
		if err != nil {
			log.WithError(err).Warn("Error converting caller models")
			return operations.NewGetUserAccountInternalServerError()
		}
	} else {
		queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
			params.ProeliumCustomerID,
			params.UserID).First(&targetUserProfileDb)
		if queryResult.RecordNotFound() {
			log.Warn("User not found")
			return operations.NewGetUserAccountNotFound()
		}

		var err error
		userProfile, err = m.convertUserAccount(targetUserProfileDb, true)
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

	// Fetch call user account
	var callerUserProfileDb dbModels.UserAccount
	queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		params.ProeliumCustomerID,
		params.ProeliumUserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewGetAllUserAccountsInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Call user not found. Operation not authorized")
		return operations.NewGetAllUserAccountsUnauthorized()
	}

	// Retrieve user profile
	var userProfilesDb []*dbModels.UserAccount

	queryResult = m.dbManager.Db.
		Order("updated_at desc").
		Where("proelium_customer_id = ?", params.ProeliumCustomerID).Find(&userProfilesDb)
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
		statsVisible := *callerUserProfileDb.IsAdmin || (userProfileDb.ProeliumUserId == params.ProeliumUserID)
		log.Info("Stats ", statsVisible)
		tmp, err := m.convertUserAccount(userProfileDb, statsVisible)
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

	queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		params.ProeliumCustomerID,
		params.ProeliumUserID).First(&callerUserProfileDb)
	if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		log.WithError(queryResult.Error).Warn("Error fetching caller user account")
		return operations.NewUpdateUserAccountInternalServerError()
	}
	if queryResult.RecordNotFound() {
		log.Warn("Call user not found. Operation not authorized")
		return operations.NewUpdateUserAccountUnauthorized()
	}

	// Check if caller is admin or is the owner of the profile
	if !*callerUserProfileDb.IsAdmin && (params.ProeliumUserID != params.UserID) {
		log.Warn("Non admin user or non owner tried to edit profile")
		return operations.NewUpdateUserAccountUnauthorized()
	}

	// Users cannot edit their own permissions and admins cannot downgrade their own accounts
	if params.UserAccount.GovernanceProfile != nil {
		if (*callerUserProfileDb.IsAdmin && (params.ProeliumUserID == params.UserID)) || !*callerUserProfileDb.IsAdmin {
			log.Warn("User not allowed to change permissions")
			return operations.NewUpdateUserAccountUnauthorized()
		}
	}

	var currUserProfileDb *dbModels.UserAccount
	if params.ProeliumUserID == params.UserID {
		currUserProfileDb = &callerUserProfileDb
	} else {
		var tmp dbModels.UserAccount

		queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
			params.ProeliumCustomerID,
			params.UserID).First(&tmp)
		if queryResult.RecordNotFound() {
			log.Warn("User bot found")
			return operations.NewUpdateUserAccountNotFound()
		}
		currUserProfileDb = &tmp
	}
	// Retrieve governance profile
	var currGovernanceProfileDb dbModels.EmbeddedUserGovernanceProfile
	queryResult = m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		params.ProeliumCustomerID,
		params.ProeliumUserID).First(&currGovernanceProfileDb)
	if queryResult.RecordNotFound() {
		log.Warn("No governance profile found")
		return operations.NewUpdateUserAccountNotFound()
	}

	newUserProfileDb, newGovernanceProfileDb, err := dbModels.ConvertToDbModelExistingUserAccount(params.ProeliumCustomerID, params.ProeliumUserID, currGovernanceProfileDb.Name, params.UserAccount)
	if err != nil {
		log.WithError(err).Warn("Failed to update user account")
		return operations.NewUpdateUserAccountInternalServerError()
	}

	profileUpdates := m.getUserProfileUpdates(currUserProfileDb, newUserProfileDb)
	governanceProfileUpdates := m.getGovernanceProfileUpdates(&currGovernanceProfileDb, newGovernanceProfileDb)

	// Start db transaction
	tx := m.dbManager.Db.Begin()
	if len(*profileUpdates) > 0 {
		if err := tx.Model(*currUserProfileDb).Updates(*profileUpdates).Error; err != nil {
			log.WithError(err).Warn("Failed to update user profile")
			tx.Rollback()
			return operations.NewUpdateUserAccountInternalServerError()
		}
	}
	if len(*governanceProfileUpdates) > 0 {
		if err := tx.Model(currGovernanceProfileDb).Updates(*governanceProfileUpdates).Error; err != nil {
			log.WithError(err).Warn("Failed to update user governance profile")
			tx.Rollback()
			return operations.NewUpdateUserAccountInternalServerError()
		}
	}

	// Commit transaction
	if len(*profileUpdates) > 0 || len(*governanceProfileUpdates) > 0 {
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

	// TODO Implement and update

	return operations.NewDeleteUserAccountOK()
}

func (m *UserAccountsHandler) convertUserAccount(profile *dbModels.UserAccount, statsVisible bool) (*genModels.UserAccountResult, error) {
	log := m.log.WithFields(logrus.Fields{
		"method": "convertUserAccount",
	})
	var userAccount *genModels.UserAccount
	var userStats *genModels.UserStats
	//	var assignedClients []*genModels.UserAssignedClient

	// Retrieve governance profile
	var governanceProfileDb dbModels.EmbeddedUserGovernanceProfile
	queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
		profile.ProeliumCustomerId,
		profile.ProeliumUserId).First(&governanceProfileDb)
	if !queryResult.RecordNotFound() {
		userAccount = dbModels.ConvertToSwaggerModelUserAccount(profile, &governanceProfileDb)
	} else if queryResult.Error != nil && !strings.Contains(queryResult.Error.Error(), "record not found") {
		return nil, queryResult.Error
	} else {
		return nil, errors.New("failed to retrieve governance profile")
	}

	// Fetch stats
	// TODO Get stats properly with date range
	if statsVisible {
		var statsDb dbModels.UserStats
		queryResult := m.dbManager.Db.Where("proelium_customer_id = ? AND proelium_user_id = ?",
			profile.ProeliumCustomerId,
			profile.ProeliumUserId).First(&statsDb)
		if !queryResult.RecordNotFound() {
			userStats = dbModels.ConvertToSwaggerModelUserStats(&statsDb)
		}
		if queryResult.Error != nil {
			// if the error is not simply that the record was not found then return an error
			if !strings.Contains(queryResult.Error.Error(), "record not found") {
				return nil, queryResult.Error
			}
			log.Info("No stats record found for user: ", profile.ProeliumUserId)
		}

		// Create mock stats object for now
		userStats = &genModels.UserStats{
			ActiveSequencesCount: aws.Int64(5),
			EmailReplies:         aws.Int64(4),
			EmailsSent:           aws.Int64(900),
			EmailsSentInSequence: aws.Int64(700),
			MeetingsBooked:       aws.Int64(6),
			UpcomingTasksCount:   aws.Int64(12),
		}
	}

	// TODO Fetch user assigned agency clients

	return &genModels.UserAccountResult{
		ID:                  &profile.ProeliumUserId,
		UserAccount:         userAccount,
		UserAssignedClients: nil,
		UserStats:           userStats,
	}, nil
}

func (m *UserAccountsHandler) getUserProfileUpdates(currModel *dbModels.UserAccount, newModel *dbModels.UserAccount) *map[string]interface{} {
	var result = make(map[string]interface{})

	// Profile
	if newModel.IsAdmin != nil && *currModel.IsAdmin != *newModel.IsAdmin {
		result["is_admin"] = newModel.IsAdmin
	}
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
