## Makefile for building, containerizing, and running the service

# You may change service path as needed
.PHONY: gen
gen:
	@java -jar ../swagger-codegen-cli.jar generate -i ExperimentsService.yaml -l python-flask

.PHONY: deps
deps:
	# TODO: replace with something specific to your language of choice @go get ./...
