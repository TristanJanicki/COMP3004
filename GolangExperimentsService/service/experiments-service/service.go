package main

import (
	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/dynamodb"
	"github.com/go-openapi/loads"
	"github.com/pkg/errors"
	log "github.com/sirupsen/logrus"

	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/infrastructure/db/mysql"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/infrastructure/httpservice"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/gen/restapi"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/gen/restapi/operations"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/handlers"
	"github.com/TristanJanicki/COMP3004/GolangExperimentsService/pkg/server"
)

type GolangExperimentsServiceService struct {
	log *log.Logger

	sqlDbManager    *mysql.SqlDbManager
	dynamoDbManager *dynamodb.DynamoDB

	server *server.HttpServer

	// handlers go here
	experimentsHandler *handlers.ExperimentsHandler

	awsSession *session.Session
	awsConfig  *aws.Config

	shutdownChan chan struct{}
}

// Init initializes member variables and links handlers
func (s *GolangExperimentsServiceService) Init(config httpservice.ServiceConfig, listenAddress string, listenPort int) (err error) {
	s.log = log.StandardLogger()
	s.shutdownChan = make(chan struct{})

	s.awsSession, s.awsConfig = NewAwsSession()
	s.dynamoDbManager = dynamodb.New(s.awsSession, s.awsConfig)

	// // Initialize DB manager
	s.sqlDbManager, err = mysql.New(config)
	if err != nil {
		log.WithError(err).Error("Failed to initialize database connection")
		return errors.Wrapf(err, "Failed to initialize database connection")
	}

	// Initialize handlers
	s.experimentsHandler, err = handlers.NewExperimentsHandler(s.sqlDbManager)
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

	api.CreateThresholdExperimentHanlder = operations.CreateThresholdExperimentHanlderFunc(s.experimentsHandler.CreateThresholdExperiment)
	api.CreateCorrelationExperimentHandler = operations.CreateCorrelationExperimentHandlerFunc(s.experimentsHandler.CreateCorrelationExperiment)
	api.GetUserExperimentsHandler = operations.GetUserExperimentsHandlersFunc(s.experimentsHandler.GetUserExperiments)
	api.DeleteUserExperimentHandler = operations.DeleteUserExperimentHandlerFunc(s.experimentsHandler.DeleteUserExperimentHandler)

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
	sess, err := session.NewSessionWithOptions(session.Options{
		Profile: "cognito",
	})

	// sess, err := session.NewSession()

	if err != nil {
		panic(err)
	}

	config := sess.Config.Copy()
	config.Region = aws.String("us-east-1")

	return sess, config
}

func (s *GolangExperimentsServiceService) ShutdownChan() <-chan struct{} {
	return s.shutdownChan
}

func (s *GolangExperimentsServiceService) Start() {
	s.server.Start()
}

func (s *GolangExperimentsServiceService) Stop() error {
	var err error
	err = s.server.Shutdown()

	s.sqlDbManager.Close()

	return err
}

func main() {
	httpservice.Run(&GolangExperimentsServiceService{})
}
