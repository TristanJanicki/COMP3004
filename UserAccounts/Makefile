## Makefile for building the service

# You may change service path as needed
.PHONY: clean
clean:
	@rm -rf pkg/gen/
	@rm -rf service/mgmt-user-accounts-service/build/
	@rm -f coverage.txt

.PHONY: gen
gen:
	@mkdir -p ./pkg/gen
	@swagger generate server --target ./pkg/gen --name SwaggerRest --spec UserAccountsService.yaml

.PHONY: deps
deps:
	@go get ./...