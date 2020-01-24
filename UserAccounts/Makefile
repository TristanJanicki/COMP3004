## Makefile for building, containerizing, and running the service
#
# DO NOT EDIT BUILD COMMANDS, unless it is indicated that it is safe to do so. Editing commands can break the
# entire build process.
#

GO_OS := $(shell go env GOOS)
GO_ARCH := $(shell go env GOARCH)
GO_FLAGS :=
ALLOW_DESTRUCTIVE_UPDATE := "false"

SCRIPT_BUILD_DIR=build/bin

ENV := dev
GIT_BRANCH := $(shell git rev-parse --abbrev-ref HEAD)

# You may change these lines as needed
SERVICE_NAME:=$(shell grep "ServiceName" service/mgmt-user-accounts-service/service.yaml | cut -d: -f2 | sed -e 's/^[[:space:]]*"//' -e 's/"[[:space:]]*$$//')
SERVICE_VERSION:=$(shell grep "ServiceVersion" service/mgmt-user-accounts-service/service.yaml | cut -d: -f2 | sed -e 's/^[[:space:]]*"//' -e 's/"[[:space:]]*$$//')

all: clean gen deps test

# You may change service path as needed
.PHONY: clean
clean:
	@rm -rf pkg/gen/
	@rm -rf service/mgmt-user-accounts-service/build/
	@rm -f coverage.txt

.PHONY: gen
gen:
	@mkdir -p ./pkg/gen
	@swagger generate server --target ./pkg/gen --name SwaggerRest --spec swagger.yaml

.PHONY: deps
deps:
	@go get ./...

.PHONY: test
test:
	@./go-test.sh

# This is disabled for now
.PHONY: images
images: GO_OS := linux
images: GO_ARCH := amd64
images: build images-only

# You may change service path as needed
.PHONY: build
build:
	@go vet service/mgmt-user-accounts-service/*.go
	GOOS=$(GO_OS) GOARCH=$(GO_ARCH) CGO_ENABLED=0 go build $(GO_FLAGS) -o service/mgmt-user-accounts-service/build/$(GO_OS)-$(GO_ARCH)/mgmt-user-accounts-service/mgmt-user-accounts-service/*.go

# You may change image name as needed
# This is disabled for now
.PHONY: images-only mgmt-user-accounts-image
images-only: mgmt-user-accounts-image
mgmt-user-accounts-service-image:
	docker build service/mgmt-user-accounts-service -t $(mgmt-user-accounts-service)-$(GIT_BRANCH):$(1)

.PHONY: run
run:
# Service dockerization is disabled for the sake of simplicity and speed of testing.
#	@docker run -it -p 8080:8080 $(SERVICE_NAME)-$(GIT_BRANCH):$(SERVICE_VERSION)
    # Starts docker containers for MySql, DynamoDb, and Consul. This is required for local testing
	@docker-compose up -d
	@sleep 3
	@go run service/mgmt-user-accounts-service/service.go

# Shuts down docker containers
.PHONY: stop
stop:
	@docker-compose stop
