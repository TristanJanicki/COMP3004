package env

import (
	"fmt"
	"os"
)

// GetServiceName returns the service name of the current application.
//
// If the service name was not set, an empty string is returned.
func GetServiceName() string {
	return os.Getenv("SERVICE_NAME")
}

// GetBranchName returns the service name of the current application.
//
// If the branch name was not set, an empty string is returned.
func GetBranchName() string {
	return os.Getenv("GIT_BRANCH")
}

// GetApiUrl returns Fabio URL that you can call to reach other services.
func GetApiUrl() string {
	return os.Getenv("FABIO_URL")
}

// GetAppName returns the application name of the current application.
//
// If the service or branch name was not set, an empty string is returned.
func GetAppName() string {
	serviceName := GetServiceName()
	branchName := GetBranchName()
	if serviceName == "" || branchName == "" {
		return ""
	}
	return fmt.Sprintf("%s-%s", serviceName, branchName)
}
