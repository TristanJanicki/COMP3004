package models

import (
	"errors"
	"time"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/go-openapi/strfmt"

	"github.com/COMP3004/UserAccounts/infrastructure/utils"
	"github.com/COMP3004/UserAccounts/pkg/gen/models"
)

type UserAccount struct {
	ProeliumUserId     string `gorm:"primary_key;column:proelium_user_id"`
	ProeliumCustomerId string `gorm:"type:varchar(36);not null;column:proelium_customer_id"`

	// Profile
	FirstName *string `gorm:"type:nvarchar(50);not null;column:first_name"`
	LastName  *string `gorm:"type:nvarchar(50);not null;column:last_name"`
	Email     *string `gorm:"type:nvarchar(320);not null;column:email"`
	NickName  *string `gorm:"type:varchar(50);column:nickname"`

	Title *string `gorm:"type:varchar(100);column:title"`

	Country     *string `gorm:"type:varchar(100);column:country"`
	CountryCode *string `gorm:"type:varchar(25);column:country_code"`
	PhoneNumber *string `gorm:"type:varchar(25);column:phone_number"`

	// Settings
	sendDailyDigest               *bool `gorm:"type:boolean;;column:send_daily_digest"`

	CreatedAt *time.Time
	UpdatedAt *time.Time
	DeletedAt *time.Time
}

func ConvertToDbModelUserAccount(customerId string, userId string, account *models.UserAccount) (*UserAccount, *EmbeddedUserGovernanceProfile, error) {

	entityId := utils.CreateEntityId()

	email := account.Profile.Email.String()
	modelDb := &UserAccount{
		ProeliumCustomerId: customerId,
		ProeliumUserId:     userId,
		IsAdmin:            aws.Bool(isAdmin),
		FirstName:          account.Profile.FirstName,
		LastName:           account.Profile.LastName,
		Email:              &email,
	}

	if len(account.Profile.NickName) > 0 {
		modelDb.NickName = &account.Profile.NickName
	}
	if len(account.Profile.Title) > 0 {
		modelDb.Title = &account.Profile.Title
	}
	// TODO Phone number validation
	if account.Profile.Phone != nil {
		modelDb.Country = account.Profile.Phone.Country
		modelDb.CountryCode = account.Profile.Phone.Code
		modelDb.PhoneNumber = account.Profile.Phone.Number
	}
	if account.Settings != nil {
		modelDb.sendDailyDigest = account.Settings.SendDailyDigest
	}

	return modelDb, governanceProfileDb, nil
}

func ConvertToDbModelExistingUserAccount(customerId string, userId string, governanceProfileId string, account *models.ExistingUserAccount) (*UserAccount, *EmbeddedUserGovernanceProfile, error) {
	modelDb := &UserAccount{
		ProeliumCustomerId: customerId,
		ProeliumUserId:     userId,
	}

	if len(account.Profile.FirstName) > 0 {
		modelDb.FirstName = &account.Profile.FirstName
	}
	if len(account.Profile.LastName) > 0 {
		modelDb.LastName = &account.Profile.LastName
	}

	if len(account.Profile.NickName) > 0 {
		modelDb.NickName = &account.Profile.NickName
	}
	if len(account.Profile.Title) > 0 {
		modelDb.Title = &account.Profile.Title
	}
	// TODO Phone number validation
	if account.Profile.Phone != nil {
		modelDb.Country = account.Profile.Phone.Country
		modelDb.CountryCode = account.Profile.Phone.Code
		modelDb.PhoneNumber = account.Profile.Phone.Number
	}
	if account.Settings != nil {
		modelDb.sendDailyDigest = account.Settings.SendDailyDigest
	}

	return modelDb, governanceProfileDb, nil
}

// Convert user profile for newly signed up user. This will be used once at sign-up
func ConvertToDbModelUserAccountSignUp(customerId string, userId string, signUpData *models.SignUpData) *UserAccount {
	email := signUpData.Email.String()
	return &UserAccount{
		ProeliumCustomerId: customerId,
		ProeliumUserId:     userId,
		IsAdmin:            aws.Bool(true),
		FirstName:          signUpData.FirstName,
		LastName:           signUpData.LastName,
		Email:              &email,
	}
}

// ConvertToSwagger converts the object it is invoked on to a swagger api model and returns that model
func ConvertToSwaggerModelUserAccount(profileDb *UserAccount, governanceProfileDb *EmbeddedUserGovernanceProfile) *models.UserAccount {
	email := strfmt.Email(*profileDb.Email)

	userProfile := &models.UserProfile{
		Email:     &email,
		FirstName: profileDb.FirstName,
		LastName:  profileDb.LastName,
	}

	if profileDb.NickName != nil {
		userProfile.NickName = *profileDb.NickName
	}
	if profileDb.Title != nil {
		userProfile.Title = *profileDb.Title
	}

	if profileDb.PhoneNumber != nil {
		userProfile.Phone = &models.PhoneNumber{
			Code:    profileDb.CountryCode,
			Country: profileDb.Country,
			Number:  profileDb.PhoneNumber,
		}
	}

	var settings *models.UserNotificationSettings
	if profileDb.sendDailyDigest != nil {
		settings = &models.UserNotificationSettings{
			SendDailyDigest:               profileDb.sendDailyDigest,
		}
	}

	return &models.UserAccount{
		Profile:           userProfile,
		Settings:          settings,
	}
}
