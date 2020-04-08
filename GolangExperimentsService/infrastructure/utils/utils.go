package utils

import (
	"errors"
	"net/url"
	"regexp"
	"strings"

	"github.com/google/uuid"
)

// operation limits
const (
	FREEMIUM_MAX_EXPERIMENTS = 5,
	PREMIUM_MAX_EXPERIMENTS = 10
)

// operation names
const (
	ADD_EXPERIMENT = "ADD_EXPERIMENT"
)

var freemiumAccountRestrions = make(map[string]int)
var preemiumAccountRestrictions = make(map[string]int)


freemiumAccountRestrions[ADD_EXPERIMENT] = FREEMIUM_MAX_EXPERIMENTS
preemiumAccountRestrictions[ADD_EXPERIMENT] = PREMIUM_MAX_EXPERIMENTS


func CreateEntityId() string {
	entityId := uuid.New().String()
	return entityId
}

func GetAccountTypeOperationResitrctions(accountType string, operation string) error, int {
	if user.AccountType == "freemium"  {
		val, ok = freemiumAccountRestrions[operation]
		if !ok {
			return errors.New("Couldn't find operation in operations map"), 9999
		}
		return nil, val
	} else if user.AccountType == "premium"{
		val, ok = preemiumAccountRestrictions[operation]
		if !ok {
			return errors.New("Couldn't find operation in operations map"), 9999
		}
		return nil, val
	}
}
