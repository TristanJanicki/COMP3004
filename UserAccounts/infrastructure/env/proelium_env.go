package env

import (
    "errors"
    "os"
)

type ProeliumEnvironment string

var (
    // ProeliumEnvironmentDev is the production environment
    ProeliumEnvironmentProd ProeliumEnvironment = "prod"

    // ProeliumEnvironmentDev is the development environment
    ProeliumEnvironmentDev ProeliumEnvironment = "dev"

    // ProeliumEnvironmentMgmt is the development environment
    ProeliumEnvironmentMgmt ProeliumEnvironment = "mgmt"

    // ProeliumEnvironmentLocalDockerTest is used during a local development, when the service is brought up in a
    // local docker container. Helpful when testing front ends locally.
    ProeliumEnvironmentLocalDockerTest ProeliumEnvironment = "localdockertest"
)

var (
    // ErrBadEnvironmentVariableSet occurs when the environment variable is not set to a valid value
    ErrBadEnvironmentVariableSet = errors.New("env variable PROELIUM_ENVIRONMENT was not 'prod', 'dev', 'mgmt', or 'dockertest'")
)

// FromEnvVariable will try to read the environment variable PROELIUM_ENVIRONMENT. If the value is not one of the
// 'prod', 'dev', 'mgmt', or 'dockertest', will return a error
func FromEnvVariable() (ProeliumEnvironment, error) {
    env := ProeliumEnvironment(os.Getenv("PROELIUM_ENVIRONMENT"))
    if !env.IsValid() {
        return "", ErrBadEnvironmentVariableSet
    }
    return env, nil
}

// IsValid returns true iff the ProeliumEnvironment is valid.
func (e ProeliumEnvironment) IsValid() bool {
    switch e {
    case ProeliumEnvironmentProd, ProeliumEnvironmentDev, ProeliumEnvironmentMgmt, ProeliumEnvironmentLocalDockerTest:
        return true
    default:
        return false
    }
}
