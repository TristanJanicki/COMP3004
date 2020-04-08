# This is a temporary docker container builder script
# TODO Build a proper custom Docker image to serve as base for all proelium Golang services

# Specify the base image we need for the service
FROM golang:1.13.0
# Create an /service directory within our image that will hold service source files
RUN mkdir /service
# Copy everything in the root directory into /service directory
COPY ./build/linux-amd64 /service
# Specify executable
WORKDIR /service
# Run go build to compile the binary executable
# RUN go build -o main .
# Start command kicks off newly created binary executable
CMD ["/service/user-accounts-service"]
