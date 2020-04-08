package dynamodb

import (
	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/aws/external"
	"github.com/aws/aws-sdk-go-v2/service/dynamodb"

	"github.com/COMP3004/GolangExperimentsService/infrastructure/httpservice"
)

func NewClient(config httpservice.ServiceConfig) *dynamodb.Client {
	cfg, _ := external.LoadDefaultAWSConfig()
	cfg.EndpointResolver = aws.ResolveWithEndpointURL(config[httpservice.DynamoDbDbEndpoint].(string))

	return dynamodb.New(cfg)
}
