package server

import (
	"log"

	"github.com/pkg/errors"
	"github.com/sirupsen/logrus"

	"github.com/COMP3004/GolangExperimentsService/infrastructure/httpservice"
	"github.com/COMP3004/GolangExperimentsService/pkg/gen/restapi"
	"github.com/COMP3004/GolangExperimentsService/pkg/gen/restapi/operations"
)

type HttpServer struct {
	log *logrus.Entry

	httpServer *restapi.Server

	shutdownChan chan struct{}

	listeningAddress string
	listeningPort    int
}

func New(api *operations.SwaggerRestAPI, address string, port int) (s *HttpServer, err error) {
	// Initialize server
	s = &HttpServer{
		log:              httpservice.DefaultLogger.WithField("type", "httpserver"),
		httpServer:       restapi.NewServer(api),
		listeningAddress: address,
		listeningPort:    port,
		shutdownChan:     make(chan struct{}),
	}
	// Configure server ports
	s.httpServer.Host = address
	s.httpServer.Port = port
	// Configure http server APIs
	s.httpServer.ConfigureAPI()

	log.Println("********************** ", s.listeningAddress, s.listeningPort)

	return s, nil
}

func (s *HttpServer) Start() {
	s.log.WithFields(logrus.Fields{
		"host": s.listeningAddress,
		"port": s.listeningPort,
	}).Info("Starting http server")

	if err := s.httpServer.Serve(); err != nil {
		s.log.WithError(err).Error("Http server failed to start")
	}

	close(s.shutdownChan)
}

func (s *HttpServer) Shutdown() error {
	if err := s.httpServer.Shutdown(); err != nil {
		s.log.WithError(err).Warn("Http server shutdown ungracefully")
		return errors.Wrapf(nil, "Http server shutdown ungracefully")
	} else {
		s.log.Info("Http server shutdown gracefully")
	}

	return nil
}
