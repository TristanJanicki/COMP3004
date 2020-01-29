package consul

import (
	"fmt"
	"os"

	"github.com/COMP3004/UserAccounts/infrastructure/env"
)

func RegisterConsul(listenPort int) (*Client, string, error) {
	consulAddress := os.Getenv("CONSUL_ADDRESS")
	consulPort := os.Getenv("CONSUL_PORT")
	consulUri := fmt.Sprintf("%s:%s", consulAddress, consulPort)
	consulClient, err := NewClient(consulUri)

	if err != nil {
		return nil, "", err
	}

	serviceId, err := consulClient.Register(env.GetServiceName(), listenPort)
	if err != nil {
		return nil, "", err
	}

	return consulClient, serviceId, nil
}
