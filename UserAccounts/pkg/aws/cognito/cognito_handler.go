package cognito

import (
	"strings"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/cognitoidentityprovider"
	"github.com/sirupsen/logrus"
)

var log *logrus.Logger

type AwsCognitoHandler struct {
	log *logrus.Entry

	userPoolId  *string
	appClientId *string

	identityProvider *cognitoidentityprovider.CognitoIdentityProvider
}

func NewAwsCognitoHandler(awsSession *session.Session, awsConfig *aws.Config, userPoolId *string, appClientId *string) *AwsCognitoHandler {
	return &AwsCognitoHandler{
		log:              logrus.StandardLogger().WithField("type", "service/client"),
		userPoolId:       userPoolId,
		appClientId:      appClientId,
		identityProvider: cognitoidentityprovider.New(awsSession, awsConfig),
	}
}

// RegisterUserWithCognito handles the logic for creating a user profile with AWS cognito
func (c *AwsCognitoHandler) RegisterUserWithCognito(email *string, name *string, accountType *string) (*string, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "RegisterUserWithCognito",
	})

	// TODO: Fill this out with the correct attributes.
	newUserData := &cognitoidentityprovider.AdminCreateUserInput{
		DesiredDeliveryMediums: []*string{
			aws.String("EMAIL"),
		},
		Username:   email,
		UserPoolId: c.userPoolId,
		UserAttributes: []*cognitoidentityprovider.AttributeType{
			{
				Name:  aws.String("custom:account_type"),
				Value: accountType,
			},
			{
				Name:  aws.String("name"),
				Value: name,
			},
		},
	}

	out, err := c.identityProvider.AdminCreateUser(newUserData)
	if err != nil {
		log.WithError(err).Warn("Failed to create user on aws cognito")
		return nil, err
	}

	var userId *string
	for _, attribute := range out.User.Attributes {
		if strings.Contains(*attribute.Name, "sub") == true {
			log.Info(*attribute.Name + *attribute.Value)
			userId = attribute.Value
		}
	}

	if userId == nil {
		log.WithError(err).Warn("Failed to create user on aws cognito")
		return nil, err
	}

	return userId, nil
}

func (c *AwsCognitoHandler) SignIn(loginEmail *string, password *string) (*cognitoidentityprovider.AdminInitiateAuthOutput, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "SignIn",
	})

	log.Info("Signing in")

	authInput := &cognitoidentityprovider.AdminInitiateAuthInput{
		AuthFlow:       aws.String("ADMIN_NO_SRP_AUTH"),
		AuthParameters: map[string]*string{"USERNAME": loginEmail, "PASSWORD": password}, // this is the format that login parameters are passed to AWS cognito.
		ClientId:       aws.String(*c.appClientId),
		UserPoolId:     aws.String(*c.userPoolId),
	}

	return c.identityProvider.AdminInitiateAuth(authInput)
}

func (c *AwsCognitoHandler) SignOut(loginEmail *string) (*cognitoidentityprovider.AdminUserGlobalSignOutOutput, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "SignOut",
	})

	log.Info("Signing out")

	input := &cognitoidentityprovider.AdminUserGlobalSignOutInput{
		UserPoolId: c.userPoolId,
		Username:   aws.String(*loginEmail),
	}

	return c.identityProvider.AdminUserGlobalSignOut(input)
}

func (c *AwsCognitoHandler) PasswordChange(oldPassword *string, newPassword *string, accessToken *string) (*cognitoidentityprovider.ChangePasswordOutput, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "PasswordChange",
	})

	log.Info("Change password")

	input := &cognitoidentityprovider.ChangePasswordInput{
		AccessToken:      accessToken,
		PreviousPassword: oldPassword,
		ProposedPassword: newPassword,
	}

	return c.identityProvider.ChangePassword(input)
}

func (c *AwsCognitoHandler) RequestPasswordRecovery(loginEmail *string) error {
	log := c.log.WithFields(logrus.Fields{
		"method": "RequestPasswordRecovery",
	})

	// TODO: make db query for user id as that is the username in the user pool

	input := &cognitoidentityprovider.AdminResetUserPasswordInput{
		UserPoolId: c.userPoolId,
		Username:   aws.String(*loginEmail),
	}

	_, err := c.identityProvider.AdminResetUserPassword(input)
	if err != nil {
		log.WithError(err).Info("Failed to reset user password")
		return err
	}

	return nil
}

func (c *AwsCognitoHandler) CompletePasswordRecovery(confirmationCode *string, loginEmail *string, newPassword *string) (*cognitoidentityprovider.ConfirmForgotPasswordOutput, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "CompletePasswordRecovery",
	})

	log.Info("CompletePasswordRecovery")

	input := &cognitoidentityprovider.ConfirmForgotPasswordInput{
		ClientId:         c.appClientId,
		ConfirmationCode: confirmationCode,
		Password:         newPassword,
		Username:         aws.String(*loginEmail),
	}

	return c.identityProvider.ConfirmForgotPassword(input)
}

func (c *AwsCognitoHandler) PasswordChallenge(sessionId *string, username *string, newPassword *string) error {
	log := c.log.WithFields(logrus.Fields{
		"method": "PasswordChallenge",
	})

	responses := map[string]*string{"NEW_PASSWORD": newPassword, "USERNAME": username}

	input := &cognitoidentityprovider.AdminRespondToAuthChallengeInput{
		ChallengeName:      aws.String("NEW_PASSWORD_REQUIRED"),
		Session:            sessionId,
		ChallengeResponses: responses,
		ClientId:           c.appClientId,
		UserPoolId:         c.userPoolId,
	}

	_, err := c.identityProvider.AdminRespondToAuthChallenge(input)

	if err != nil {
		log.WithError(err).Warn("Password challenge failed")
		return err
	}

	// Mark user email as verified
	attributesInput := &cognitoidentityprovider.AdminUpdateUserAttributesInput{
		UserAttributes: []*cognitoidentityprovider.AttributeType{
			{
				Name:  aws.String("email_verified"),
				Value: aws.String("true"),
			},
		},
		UserPoolId: c.userPoolId,
		Username:   username,
	}

	_, err = c.identityProvider.AdminUpdateUserAttributes(attributesInput)
	if err != nil {
		log.WithError(err).Warn("Failed to update user attributes")
		return err
	}

	return nil
}

func (c *AwsCognitoHandler) RefreshTokens(refreshToken *string) (*string, *string, *string, error) {
	log := c.log.WithFields(logrus.Fields{
		"method": "RefreshTokens",
	})

	log.Info("RefreshTokens")

	input := &cognitoidentityprovider.AdminInitiateAuthInput{
		AuthFlow:       aws.String("REFRESH_TOKEN_AUTH"),
		AuthParameters: map[string]*string{"REFRESH_TOKEN": refreshToken},
		ClientId:       c.appClientId,
		UserPoolId:     c.userPoolId,
	}

	out, err := c.identityProvider.AdminInitiateAuth(input)

	if err != nil {
		return nil, nil, nil, err
	}

	return out.AuthenticationResult.IdToken, out.AuthenticationResult.RefreshToken, out.AuthenticationResult.AccessToken, nil
}

func (c *AwsCognitoHandler) DeleteUserFromCognito(email *string) error {
	log := c.log.WithFields(logrus.Fields{
		"method": "DeleteUserFromCognito",
	})

	deleteUserData := &cognitoidentityprovider.AdminDeleteUserInput{
		UserPoolId: c.userPoolId,
		Username:   email,
	}

	_, err := c.identityProvider.AdminDeleteUser(deleteUserData)
	if err != nil {
		log.WithError(err).Warn("Failed to delete user from aws cognito")
		return err
	}

	return nil
}
