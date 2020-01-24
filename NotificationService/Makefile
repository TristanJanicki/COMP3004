## Makefile for building, containerizing, and running the service

# You may change service path as needed
.PHONY: clean
clean:
	@rm -rf pkg/gen/
	@rm -rf service/notifications-service/build/
	@rm -f coverage.txt

.PHONY: gen
gen:
	@mkdir -p ./pkg/gen
	@swagger generate server --target ./pkg/gen --name SwaggerRest --spec swagger.yaml