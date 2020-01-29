package utils

import (
	"errors"
	"fmt"
	"os"

	"github.com/dgrijalva/jwt-go"
	"github.com/lestrrat-go/jwx/jwk"
	"github.com/sirupsen/logrus"
)

func VerifyJwtToken(token string) (bool, error) {
	log := *logrus.StandardLogger().WithFields(logrus.Fields{
		"method": "VerifyJwtToken",
	})

	parsedToken, err := jwt.Parse(token, getKey)
	if err != nil {
		log.Warn("Error verifying JWT Token", err.Error())
		return false, err
	}

	if !parsedToken.Valid {
		return false, nil
	}
	return true, nil
}

func getKey(token *jwt.Token) (interface{}, error) {
	log := *logrus.StandardLogger().WithFields(logrus.Fields{
		"method": "getKey",
	})
	// TODO: cache response by storing it in memory when the service starts
	// we want to verify a JWT

	userPoolID := os.Getenv("userPoolID")
	if userPoolID == "" {
		log.Warn("userPoolID Env Variable not set")
		return nil, errors.New("userPoolID Env Variable not set")
	}

	set, err := jwk.Fetch("https://cognito-idp.us-east-1.amazonaws.com/" + userPoolID + "/.well-known/jwks.json")
	if err != nil {
		log.Warn("Error getting key ", err.Error())
		return nil, err
	}
	if err != nil {
		return nil, err
	}
	keyID, ok := token.Header["kid"].(string)
	if !ok {
		return nil, errors.New("expecting JWT header to have string kid")
	}
	if key := set.LookupKeyID(keyID); len(key) == 1 {
		return key[0].Materialize()
	}
	return nil, fmt.Errorf("unable to find key %q", keyID)
}
