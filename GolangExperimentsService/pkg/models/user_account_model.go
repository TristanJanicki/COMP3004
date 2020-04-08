package models

import (
	"time"

	"github.com/go-openapi/strfmt"

	"github.com/COMP3004/UserAccounts/pkg/gen/models"
)

type UserAccount struct {
	UserId      *string `gorm:"primary_key;column:user_id"`
	AccountType *string `gorm:"type:nvarchar(50);not null;column:first_name"`

	// Profile
	FirstName *string `gorm:"type:nvarchar(50);not null;column:first_name"`
	LastName  *string `gorm:"type:nvarchar(50);not null;column:last_name"`
	Email     *string `gorm:"type:nvarchar(320);not null;column:email"`
	NickName  string  `gorm:"type:varchar(50);column:nickname"`

	Title string `gorm:"type:varchar(100);column:title"`

	Country     string `gorm:"type:varchar(100);column:country"`
	CountryCode string `gorm:"type:varchar(25);column:country_code"`
	PhoneNumber string `gorm:"type:varchar(25);column:phone_number"`

	Experiments string `gorm:"type:text;column:experiments"`

	// Settings
	sendDailyDigest *bool `gorm:"type:boolean;;column:send_daily_digest"`

	CreatedAt *time.Time
	UpdatedAt *time.Time
	DeletedAt *time.Time
}

func ConvertToDbModelUserAccount(userId string, account *models.UserAccount) (*UserAccount, error) {

	email := account.Profile.Email.String()
	modelDb := &UserAccount{
		UserId:    &userId,
		FirstName: account.Profile.FirstName,
		LastName:  account.Profile.LastName,
		Email:     &email,
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

	return modelDb, nil
}

func ConvertToDbModelExistingUserAccount(userId string, account *models.ExistingUserAccount) (*UserAccount, error) {
	modelDb := &UserAccount{
		UserId: &userId,
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

	return modelDb, nil
}

// Convert user profile for newly signed up user. This will be used once at sign-up
func ConvertToDbModelUserAccountSignUp(userId string, signUpData *models.SignUpData) *UserAccount {
	email := signUpData.Email.String()
	return &UserAccount{
		UserId:    &userId,
		FirstName: signUpData.FirstName,
		LastName:  signUpData.LastName,
		Email:     &email,
	}
}

// ConvertToSwagger converts the object it is invoked on to a swagger api model and returns that model
func ConvertToSwaggerModelUserAccount(profileDb *UserAccount) *models.UserAccount {
	email := strfmt.Email(*profileDb.Email)

	userProfile := &models.UserProfile{
		Email:     &email,
		FirstName: profileDb.FirstName,
		LastName:  profileDb.LastName,
	}

	if len(profileDb.NickName) != 0 {
		userProfile.NickName = profileDb.NickName
	}
	if len(profileDb.Title) != 0 {
		userProfile.Title = profileDb.Title
	}

	if len(profileDb.PhoneNumber) != 0 {
		userProfile.Phone = &models.PhoneNumber{
			Code:    profileDb.CountryCode,
			Country: profileDb.Country,
			Number:  profileDb.PhoneNumber,
		}
	}

	var settings *models.UserNotificationSettings
	if profileDb.sendDailyDigest != nil {
		settings = &models.UserNotificationSettings{
			SendDailyDigest: profileDb.sendDailyDigest,
		}
	}

	return &models.UserAccount{
		Profile:  userProfile,
		Settings: settings,
	}
}
