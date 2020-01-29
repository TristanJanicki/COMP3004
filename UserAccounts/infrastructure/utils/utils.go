package utils

import (
	"errors"
	"net/url"
	"regexp"
	"strings"

	"github.com/google/uuid"
)

var ValidDomain = regexp.MustCompile(`(?:[a-z0-9](?:[a-z0-9-]{0,61}[a-z0-9])?\.)+[a-z0-9][a-z0-9-]{0,61}[a-z0-9]`)

func CreateCustomerId() string {
	customerId := uuid.New().String()
	return customerId
}

func CreateEntityId() string {
	entityId := uuid.New().String()
	return entityId
}

func ExtractDomain(website string) (*string, error) {
	u, err := url.ParseRequestURI(website)
	if err != nil {
		results := ValidDomain.FindAllString(website, 1)
		if results == nil || len(results) == 0 {
			return nil, errors.New("invalid website or domain")
		} else {
			domain := strings.ToLower(strings.Replace(results[0], "www.", "", 1))
			return &domain, nil
		}
	} else {
		if !strings.Contains(website, "http://") && !strings.Contains(website, "https://") {
			return nil, errors.New("invalid website or domain")
		}
		hostname := u.Hostname()
		domain := strings.ToLower(strings.Replace(hostname, "www.", "", 1))
		return &domain, nil
	}
}

func CompareDomain(email string, domain string) bool {
	email = strings.ToLower(email)
	domain = strings.ToLower(domain)

	components := strings.Split(email, "@")
	if len(components) != 2 {
		return false
	}
	return strings.Compare(components[1], domain) == 0
}
