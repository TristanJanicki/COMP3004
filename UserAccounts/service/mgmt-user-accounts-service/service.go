package main

import (
	"os"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/credentials"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/go-openapi/loads"
	"github.com/pkg/errors"
	log "github.com/sirupsen/logrus"

	"github.com/COMP3004/UserAccounts/infrastructure/db/mysql"
	"github.com/COMP3004/UserAccounts/infrastructure/httpservice"
	"github.com/COMP3004/UserAccounts/pkg/aws/cognito"
	"github.com/COMP3004/UserAccounts/pkg/gen/restapi"
	"github.com/COMP3004/UserAccounts/pkg/gen/restapi/operations"
	"github.com/COMP3004/UserAccounts/pkg/handlers"
	"github.com/COMP3004/UserAccounts/pkg/server"
)

type MgmtUserAccountsService struct {
	log *log.Logger

	sqlDbManager *mysql.SqlDbManager

	server *server.HttpServer

	cognitoHandler         *cognito.AwsCognitoHandler
	userAccountsHandler    *handlers.UserAccountsHandler
	authHandler            *handlers.AuthenticationHandler

	awsSession *session.Session
	awsConfig  *aws.Config

	shutdownChan chan struct{}
}

// Init initializes member variables and links handlers
func (s *MgmtUserAccountsService) Init(config httpservice.ServiceConfig, listenAddress string, listenPort int) (err error) {
	s.log = log.StandardLogger()
	s.shutdownChan = make(chan struct{})

	// Initialize cognito handler
	userPoolId := os.Getenv("quantrUserPoolID")
	appClientId := os.Getenv("quantrAppClientID")
	if len(userPoolId) == 0 || len(appClientId) == 0 {
		s.log.Error("missing user pool or app client id. Exiting...")
		return errors.Wrapf(err, "Failed to load server spec")
	}
	s.awsSession, s.awsConfig = NewAwsSession()
	s.cognitoHandler = cognito.NewAwsCognitoHandler(s.awsSession, s.awsConfig, &userPoolId, &appClientId)

	// Initialize DB manager
	s.sqlDbManager, err = mysql.New(config)
	if err != nil {
		log.WithError(err).Error("Failed to initialize database connection")
		return errors.Wrapf(err, "Failed to initialize database connection")
	}

	// Initialize handlers
	s.authHandler = handlers.NewAuthenticationHandler(s.sqlDbManager, s.cognitoHandler)
	s.userAccountsHandler, err = handlers.NewUserAccountsHandler(s.sqlDbManager, s.cognitoHandler)
	if err != nil {
		log.WithError(err).Error("Failed to initialize user accounts handler")
		return errors.Wrapf(err, "Failed to initialize user accounts handler")
	}

	// Initialize API
	swaggerSpec, err := loads.Embedded(restapi.SwaggerJSON, restapi.FlatSwaggerJSON)
	if err != nil {
		log.WithError(err).Error("Failed to load server spec")
		return errors.Wrapf(err, "Failed to load server spec")
	}
	api := operations.NewSwaggerRestAPI(swaggerSpec)

	// Link handlers
	api.SignUpHandler = operations.SignUpHandlerFunc(s.authHandler.SignUp)
	api.RequestPasswordRecoveryHandler = operations.RequestPasswordRecoveryHandlerFunc(s.authHandler.RequestPasswordRecovery)
	api.CompletePasswordRecoveryHandler = operations.CompletePasswordRecoveryHandlerFunc(s.authHandler.CompletePasswordRecovery)
	api.ChangePasswordHandler = operations.ChangePasswordHandlerFunc(s.authHandler.ChangePassword)
	api.SignInHandler = operations.SignInHandlerFunc(s.authHandler.SignIn)
	api.SignOutHandler = operations.SignOutHandlerFunc(s.authHandler.SignOut)
	api.CompleteAuthChallengeHandler = operations.CompleteAuthChallengeHandlerFunc(s.authHandler.CompletePasswordChallenge)
	api.RefreshTokensHandler = operations.RefreshTokensHandlerFunc(s.authHandler.RefreshTokens)
	api.VerifyJwtHandler = operations.VerifyJwtHandlerFunc(s.authHandler.VerifyJwt)

	api.CreateUserAccountHandler = operations.CreateUserAccountHandlerFunc(s.userAccountsHandler.CreateUserAccount)
	api.UpdateUserAccountHandler = operations.UpdateUserAccountHandlerFunc(s.userAccountsHandler.UpdateUserAccount)
	api.GetUserAccountHandler = operations.GetUserAccountHandlerFunc(s.userAccountsHandler.GetUserAccount)
	api.GetAllUserAccountsHandler = operations.GetAllUserAccountsHandlerFunc(s.userAccountsHandler.GetAllUserAccounts)
	api.DeleteUserAccountHandler = operations.DeleteUserAccountHandlerFunc(s.userAccountsHandler.DeleteUserAccount)

	// Init http server
	s.server, err = server.New(api, listenAddress, listenPort)
	if err != nil {
		log.WithError(err).Error("Failed to initialize http server")
		return errors.Wrapf(err, "Failed to initialize http server")
	}

	return nil
}

// NewAwsSession create a new aws session using the credentials of the profile tristan
func NewAwsSession() (s *session.Session, c *aws.Config) {
	// cfg, _ := external.LoadDefaultAWSConfig()
	// cfg.EndpointResolver = aws.ResolveWithEndpointURL(config[httpservice.DynamoDbDbEndpoint].(string))

	config := &aws.Config{
		CredentialsChainVerboseErrors: aws.Bool(true),
		Region:                        aws.String("us-east-1"),
		Credentials:                   credentials.NewSharedCredentials("", "tristan"),
	}

	sess, err := session.NewSession()

	if err != nil {
		panic(err)
	}

	return sess, config
}

func (s *MgmtUserAccountsService) ShutdownChan() <-chan struct{} {
	return s.shutdownChan
}

func (s *MgmtUserAccountsService) Start() {
	s.server.Start()
}

func (s *MgmtUserAccountsService) Stop() error {
	var err error
	err = s.server.Shutdown()

	s.sqlDbManager.Close()

	return err
}

func main() {
	httpservice.Run(&MgmtUserAccountsService{})
}
