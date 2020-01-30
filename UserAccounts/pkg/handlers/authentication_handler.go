package handlers

import (
	b64 "encoding/base64"
	"encoding/json"
	"errors"
	"strings"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/go-openapi/runtime/middleware"
	"github.com/sirupsen/logrus"

	"github.com/COMP3004/UserAccounts/infrastructure/db/mysql"
	"github.com/COMP3004/UserAccounts/pkg/aws/cognito"
	genModels "github.com/COMP3004/UserAccounts/pkg/gen/models"
	"github.com/COMP3004/UserAccounts/pkg/gen/restapi/operations"
	"github.com/COMP3004/UserAccounts/pkg/models"
	dbModels "github.com/COMP3004/UserAccounts/pkg/models"
	"github.com/COMP3004/UserAccounts/pkg/utils"
)

// AuthenticationHandler is the object definition for the holding the various
type AuthenticationHandler struct {
	cognitoHandler *cognito.AwsCognitoHandler
	dbManager      *mysql.SqlDbManager
	log            *logrus.Entry
}

// the length in seconds that the authentication result is valid
var AuthResultExpiry int64 = 36000

// NewAuthenticationHandler Creates a new authentication handler to handler authenticating with aws cognito
func NewAuthenticationHandler(db *mysql.SqlDbManager, cognitoHandler *cognito.AwsCognitoHandler) *AuthenticationHandler {
	return &AuthenticationHandler{
		dbManager:      db,
		log:            logrus.StandardLogger().WithField("type", "authentication_handler"),
		cognitoHandler: cognitoHandler,
	}
}

func (handler *AuthenticationHandler) SignUp(params operations.SignUpParams) middleware.Responder {
	log := handler.log.WithFields(logrus.Fields{
		"method": "SignUp",
	})

	email := params.SignUpData.Email.String()
	userId, err := handler.cognitoHandler.RegisterUserWithCognito(&email)
	if err != nil {
		log.WithError(err).Warn("Sign up fail")
		if strings.Contains(err.Error(), "UsernameExistsException") == true {
			log.Info("Returning 409")
			return operations.NewSignUpConflict().WithPayload(&genModels.AlreadyExistsResponse{
				Message: aws.String("Username Already In Use"),
			})
		}
		return operations.NewSignUpInternalServerError()
	}

	userAccountDb := models.ConvertToDbModelUserAccountSignUp(*userId, params.SignUpData)

	// Create admin governance profile for first user

	tx := handler.dbManager.Db.Begin()
	if err := tx.Create(userAccountDb).Error; err != nil {
		log.WithError(err).Warn("Failed to create new user account")
		tx.Rollback()
		handler.cognitoHandler.DeleteUserFromCognito(&email)
		return operations.NewSignUpInternalServerError()
	}

	// Commit transaction
	if err := tx.Commit().Error; err != nil {
		log.WithError(err).Warn("Failed to commit to db")
		tx.Rollback()
		handler.cognitoHandler.DeleteUserFromCognito(&email)
		return operations.NewSignUpInternalServerError()
	}

	// TODO Start trial period

	return operations.NewSignUpCreated()
}

// CompletePasswordChallenge expects a newPassword from the POST request. It uses this new password and the cognito session ID and username (both stored in a cookie when the user logs in) to complete the aws cognito NEW_PASSWORD_REQUIRED authentication challenge.
func (handler *AuthenticationHandler) CompletePasswordChallenge(params operations.CompleteAuthChallengeParams) middleware.Responder {
	log := handler.log.WithFields(logrus.Fields{
		"method": "CompletePasswordChallenge",
	})
	log.Info("attempting to complete password challenge")

	cognitoSession := params.AuthChallengeCredentials.SessionID
	username := params.AuthChallengeCredentials.Email.String()
	newPassword := params.AuthChallengeCredentials.NewPassword

	err := handler.cognitoHandler.PasswordChallenge(cognitoSession, &username, newPassword)

	if err != nil {
		log.WithError(err)
		return operations.NewCompleteAuthChallengeInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String("password challenge failed due to unknown error"),
		})
	}

	return operations.NewCompleteAuthChallengeCreated()
}

// AuthenticateWithCognito attempts to sign a user in
func (handler *AuthenticationHandler) SignIn(params operations.SignInParams) middleware.Responder {

	log := handler.log.WithFields(logrus.Fields{
		"method": "SignIn",
	})

	loginEmail := params.LoginCredentials.Email.String()
	password := params.LoginCredentials.Password.String()

	cognitoOutput, err := handler.cognitoHandler.SignIn(&loginEmail, &password)

	if err != nil {
		log.WithError(err).Warn("Error during sign in ")
		errStr := err.Error()
		errParts := strings.Split(errStr, ":")

		if len(errParts) == 0 {
			log.Info("Error parts is empty")
			return operations.NewSignInInternalServerError()
		}

		errStr = errParts[0]
		if strings.Compare(errStr, "NotAuthorizedException") == 0 {
			return operations.NewSignInUnauthorized().WithPayload(&genModels.NotAllowedResponse{
				Message: aws.String("Incorrect username or password"),
			})
		}

		// This can be thrown by cognito for many reasons, e.x. a non-existent username/password combo
		if strings.Compare(errStr, "InvalidParameterException") == 0 {
			log.Info(err.Error())
			return operations.NewSignInInternalServerError().WithPayload(&genModels.ErrorResponse{
				Message: aws.String("Invalid Username/Password"),
			})
		}

		// this means the user forgot their password, AdminResetUserPassword was called and they now need to reset their password
		if strings.Compare(errStr, "PasswordResetRequiredException") == 0 {
			return operations.NewSignInTemporaryRedirect().WithPayload(&genModels.AuthChallengeRequiredResult{
				Email:         &loginEmail,
				SessionID:     cognitoOutput.Session,
				ChallengeName: aws.String("PASSWORD_RESET_REQUIRED"),
			})
		}

		return operations.NewSignInNotFound().WithPayload(&genModels.NotFoundResponse{
			Message: &errStr,
		})
	}

	if cognitoOutput.AuthenticationResult == nil {
		log.Info("Challenge Required")

		// an authentication challenge needs to be completed
		if strings.Compare(*cognitoOutput.ChallengeName, "NEW_PASSWORD_REQUIRED") == 0 {
			userData := make(map[string]string)
			json.Unmarshal([]byte(*cognitoOutput.ChallengeParameters["userAttributes"]), &userData)
			cognitoEmail := userData["email"]
			log.Info("SESSION ID: ", *cognitoOutput.Session)

			return operations.NewSignInTemporaryRedirect().WithPayload(&genModels.AuthChallengeRequiredResult{
				Email:         &cognitoEmail,
				SessionID:     cognitoOutput.Session,
				ChallengeName: cognitoOutput.ChallengeName,
			})
		} // other challenge types are unhandled right now though I don't think there will be any since new password is the only applicable one at this time
	}

	// all has gone well
	log.Info("Successful Authenticate With Cognito")

	cognitoOutput.AuthenticationResult.SetExpiresIn(AuthResultExpiry)

	return operations.NewSignInCreated().WithPayload(&genModels.TokenResponse{
		IDToken:      cognitoOutput.AuthenticationResult.IdToken,
		AccessToken:  cognitoOutput.AuthenticationResult.AccessToken,
		RefreshToken: cognitoOutput.AuthenticationResult.RefreshToken,
	})
}

// parseCognitoIdToken takes the base64 encoded jwt token returned by a successful cognito login and parses the user's profile attributes from it.
// if an error occurs an empty CognitoAttributes struct is returned.
func parseCognitoIdToken(idToken string) (*string, error) {
	idParts := strings.Split(idToken, ".")

	log := logrus.WithFields(logrus.Fields{
		"method": "parseCognitoIdToken",
	})

	if len(idParts) != 3 {
		log.Warn("ID Token does NOT contain 3 parts")
		return nil, errors.New("ID Token does NOT contain 3 parts")
	}

	userAttrStr, err := b64.RawStdEncoding.DecodeString(idParts[1])
	if err != nil {
		log.Warn(err.Error())
		return nil, err
	}
	return aws.String(string(userAttrStr)), nil
}

// SignOutWithCognito signs a user out of all devices.
func (handler *AuthenticationHandler) SignOut(params operations.SignOutParams) middleware.Responder {
	log := handler.log.WithFields(logrus.Fields{
		"method": "SignOut",
	})

	_, err := utils.VerifyJwtToken(params.IDToken)
	if err != nil {
		log.Warn(err.Error())

		if strings.Contains(err.Error(), "NotAuthorizedException") == true {
			return operations.NewSignOutUnauthorized()
		}
		if strings.Contains(strings.ToLower(err.Error()), "expired") == true {
			return operations.NewSignOutUnauthorized()
		}
		return operations.NewSignOutInternalServerError()
	}

	cognitoAttrs := &models.CognitoAttributes{}
	parsedToken, err := parseCognitoIdToken(params.IDToken) // replace with looked up username
	if err != nil {
		return operations.NewSignOutInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String(err.Error()),
		})
	}

	err = json.Unmarshal([]byte(*parsedToken), &cognitoAttrs)
	if err != nil {
		log.Warn(err.Error())
		return operations.NewSignOutInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String(err.Error()),
		})
	}

	// Sign out
	_, err = handler.cognitoHandler.SignOut(&cognitoAttrs.Email)
	if err != nil {
		log.Warn(err.Error())
		return operations.NewSignOutInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String(err.Error()),
		})
	}
	return operations.NewSignOutOK()
}

// ChangePassword is the handler used when a user wants to update their password
func (handler *AuthenticationHandler) ChangePassword(params operations.ChangePasswordParams) middleware.Responder {

	log := handler.log.WithField("method", "ChangePassword")

	oldPassword := params.ChangePasswordInput.OldPassword
	newPassword := params.ChangePasswordInput.NewPassword
	accessToken := params.ChangePasswordInput.AccessToken

	_, err := handler.cognitoHandler.PasswordChange(oldPassword, newPassword, accessToken)
	if err != nil {
		log.WithError(err).Warn("Failed to change password")
		return operations.NewChangePasswordInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String(err.Error()),
		})
	}

	return operations.NewChangePasswordOK()
}

// RequestPasswordRecovery is the handler used when a user forgot their password and needs to have it reset by the identity provider
func (handler *AuthenticationHandler) RequestPasswordRecovery(params operations.RequestPasswordRecoveryParams) middleware.Responder {
	log := handler.log.WithField("method", "RequestPasswordRecovery")

	email := params.Email.String()

	var userProfile dbModels.UserAccount
	queryResult := handler.dbManager.Db.Where("email = ?", email).First(&userProfile)

	if queryResult.RecordNotFound() {
		log.Warn("No user found with this email ", email)
		return operations.NewChangePasswordNotFound()
	}
	if queryResult.Error != nil {
		log.WithError(queryResult.Error).Warn("Error querying user profile for request password reset")
		return operations.NewChangePasswordInternalServerError()
	}

	err := handler.cognitoHandler.RequestPasswordRecovery(&userProfile.UserId)

	if err != nil {
		log.WithError(err).Warn("Failed password recovery")
		return operations.NewRequestPasswordRecoveryInternalServerError().WithPayload(&genModels.ErrorResponse{
			Message: aws.String(err.Error()),
		})
	}

	return operations.NewRequestPasswordRecoveryOK()
}

func (handler *AuthenticationHandler) CompletePasswordRecovery(params operations.CompletePasswordRecoveryParams) middleware.Responder {
	log := handler.log.WithField("method", "CompletePasswordRecovery")

	_, err := handler.cognitoHandler.CompletePasswordRecovery(params.ConfirmRecoverPasswordInput.ConfirmationCode, aws.String(params.ConfirmRecoverPasswordInput.Email.String()), params.ConfirmRecoverPasswordInput.NewPassword)

	if err != nil {
		log.WithError(err).Warn("Failed to recover password")
		return operations.NewCompletePasswordRecoveryInternalServerError()
	}

	return operations.NewCompletePasswordRecoveryCreated()
}

func (handler *AuthenticationHandler) VerifyJwt(params operations.VerifyJwtParams) middleware.Responder {
	log := handler.log.WithField("method", "VerifyJWT")

	ok, err := utils.VerifyJwtToken(params.Token)

	if err != nil {
		log.Info(err.Error())
		return operations.NewVerifyJwtUnauthorized()
	}

	if ok == false {
		log.Info(err.Error())
		return operations.NewVerifyJwtUnauthorized()
	}

	return operations.NewVerifyJwtOK()
}

//RefreshToken takes in a Refresh token and uses it to get new access, refresh, and id tokens from Cognito
func (handler *AuthenticationHandler) RefreshTokens(params operations.RefreshTokensParams) middleware.Responder {
	log := handler.log.WithField("method", "RefreshTokens")

	idToken, _, accessToken, err := handler.cognitoHandler.RefreshTokens(&params.RefreshToken)
	if err != nil {
		log.WithError(err).Warn("Failed to refresh access token")

		if strings.Contains(err.Error(), "NotAuthorizedException") {
			return operations.NewRefreshTokensUnauthorized()
		}
		return operations.NewRefreshTokensInternalServerError()
	}

	return operations.NewRefreshTokensOK().WithPayload(&genModels.TokenResponse{
		IDToken:     idToken,
		AccessToken: accessToken,
	})
}
