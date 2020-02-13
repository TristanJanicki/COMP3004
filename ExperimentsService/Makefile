## Makefile for building, containerizing, and running the service

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
	# TODO: replace with something specific to your language of choice @go get ./...
