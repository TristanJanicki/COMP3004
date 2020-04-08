package env

import (
	"errors"
	"os"
)

type QuantrEnvironment string

var (
	// QuantrEnvironmentDev is the production environment
	QuantrEnvironmentProd QuantrEnvironment = "prod"

	// QuantrEnvironmentDev is the development environment
	QuantrEnvironmentDev QuantrEnvironment = "dev"

	// QuantrEnvironmentMgmt is the development environment
	QuantrEnvironmentMgmt QuantrEnvironment = "mgmt"

	// QuantrEnvironmentLocalDockerTest is used during a local development, when the service is brought up in a
	// local docker container. Helpful when testing front ends locally.
	QuantrEnvironmentLocalDockerTest QuantrEnvironment = "localdockertest"
)

var (
	// ErrBadEnvironmentVariableSet occurs when the environment variable is not set to a valid value
	ErrBadEnvironmentVariableSet = errors.New("env variable PROELIUM_ENVIRONMENT was not 'prod', 'dev', 'mgmt', or 'dockertest'")
)

// FromEnvVariable will try to read the environment variable PROELIUM_ENVIRONMENT. If the value is not one of the
// 'prod', 'dev', 'mgmt', or 'dockertest', will return a error
func FromEnvVariable() (QuantrEnvironment, error) {
	env := QuantrEnvironment(os.Getenv("PROELIUM_ENVIRONMENT"))
	if !env.IsValid() {
		return "", ErrBadEnvironmentVariableSet
	}
	return env, nil
}

// IsValid returns true iff the QuantrEnvironment is valid.
func (e QuantrEnvironment) IsValid() bool {
	switch e {
	case QuantrEnvironmentProd, QuantrEnvironmentDev, QuantrEnvironmentMgmt, QuantrEnvironmentLocalDockerTest:
		return true
	default:
		return false
	}
}
