package httpservice

import (
	"expvar"
	"flag"
	"fmt"
	"net/http"
	"net/http/pprof"
	"os"
	"os/signal"
	"strings"
	"syscall"
	"time"

	"github.com/sirupsen/logrus"
	"github.com/spf13/viper"

	"github.com/COMP3004/UserAccounts/infrastructure/env"
)

type Service interface {
	Init(config ServiceConfig, listenAddress string, listenPort int) error

	Start()

	ShutdownChan() <-chan struct{}

	Stop() error
}

const (
	// Similar to time.RFC3339Nano but millisecond precision
	logTimestampFormat = "2006-01-02T15:04:05.999Z07:00"
)

var (
	configPath = flag.String("config", "config.yaml", "configuration file path")

	loadedConfig *Config

	osSig = make(chan os.Signal, 1)
)

func init() {
	signal.Notify(osSig, syscall.SIGINT, syscall.SIGKILL, syscall.SIGTERM, syscall.SIGQUIT, syscall.SIGHUP)
}

// Run runs the provided service, setting up a http server and endpoints.
//
// Run blocks until both the service and http server have been shutdown, either
// in response to some error from the service, error from the http server, or
// an OS signal.
//
// The service life cycle is as follows:
//
//    1. Load configuration / load dependencies
//    2. Initialize the service
//    3. Run the http server
//    4. -- Serve requests --
//    5. On shutdown signal
//    6. Cleanly stop http server
//    7. Stop service
//    8. Return
func Run(service Service) error {
	flag.Parse()

	// Setup env config
	// viper.BindEnv("consul_address", "CONSUL_ADDRESS")
	viper.BindEnv("listen_address", "LISTEN_ADDRESS")
	viper.BindEnv("listen_port", "LISTEN_PORT")
	viper.BindEnv("log_level", "LOG_LEVEL")
	viper.BindEnv("log_type", "LOG_TYPE")

	// Normally exposed via `git.labels`, as with other languages, but due to
	// how viper works, in order to set the map value via environment, we have
	// to do a hack. We only do this for a select set of labels that are
	// 'required', since it makes running from the command line / ad hoc testing
	// easier.
	viper.BindEnv("git_branch", "GIT_BRANCH")
	viper.BindEnv("service_name", "SERVICE_NAME")

	logger := logrus.StandardLogger().WithField("type", "rest/app")

	var err error
	// viper.ReadInConfig only returns ConfigFileNotFoundError if it has to search
	// for a default config file because one hasn't been explicitly set.
	if _, err = os.Stat(*configPath); err == nil {
		viper.SetConfigFile(*configPath)
	} else if !os.IsNotExist(err) {
		logger.WithError(err).Errorf("failed to check if config exists")
		os.Exit(1)
	}

	err = viper.ReadInConfig()
	_, isConfigNotFound := err.(viper.ConfigFileNotFoundError)
	if err != nil && !isConfigNotFound {
		logger.WithError(err).Error("failed to load config")
		os.Exit(1)
	}

	config := DefaultConfig
	if err = viper.Unmarshal(&config); err != nil {
		logger.WithError(err).Error("failed to unmarshal config")
		os.Exit(1)
	}

	loadedConfig = &config
	configureLogger(config)

	// Override the default http ServerMux with a fresh one so we don't expose
	// pprof/expvar over a public port, whose handlers are setup in their
	// respective init() functions. This will only work if this occurs BEFORE
	// Init() where we'd expect application-level HTTP handlers to be set.
	http.DefaultServeMux = http.NewServeMux()
	debugHTTPMux := http.NewServeMux()
	if config.EnableExpvar {
		debugHTTPMux.Handle("/debug/vars", expvar.Handler())
	}
	if config.EnablePprof {
		debugHTTPMux.HandleFunc("/debug/pprof/", pprof.Index)
		debugHTTPMux.HandleFunc("/debug/pprof/cmdline", pprof.Cmdline)
		debugHTTPMux.HandleFunc("/debug/pprof/profile", pprof.Profile)
		debugHTTPMux.HandleFunc("/debug/pprof/symbol", pprof.Symbol)
		debugHTTPMux.HandleFunc("/debug/pprof/trace", pprof.Trace)
	}

	// Health check
	debugHTTPMux.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		r.Body.Close()
		w.WriteHeader(http.StatusOK)
		w.Write([]byte("OK"))
	})

	if config.EnableExpvar || config.EnablePprof {
		go func() {
			for {
				if err = http.ListenAndServe(":8123", debugHTTPMux); err != nil {
					logger.WithError(err).Error("Debug http server failed. Retrying in 5s...")
				}
				time.Sleep(5 * time.Second)
			}
		}()
	}

	// Service name is required due to various components requiring it to
	// function correctly. For example, consul registration. (NOT NEEDED FOR NOW)
	/*
	   serviceName := viper.GetString("service_name")
	   if serviceName == "" {
	       logger.Error("SERVICE_NAME was not set")
	       os.Exit(1)
	   }
	   // appName := fmt.Sprintf("%s-%s", serviceName, environmentBranch)
	   // serverShutdownChan := make(chan struct{})
	*/

	// Git branch configs
	environmentBranch := viper.GetString("git_branch")
	if environmentBranch != "" {
		config.Labels["git.branch"] = environmentBranch
	}

	// Initialize service
	fmt.Println(DefaultServiceConfig.SqlDbPassword)
	if err = service.Init(DefaultServiceConfig, config.ListenAddress, config.ListenPort); err != nil {
		logger.WithError(err).Error("Failed to initialize service")
		os.Exit(1)
	}

	// //Register with Consul NOT IN USE SINCE WE PROBABLY WON'T BOTHER WITH MULTIPLE INSTANCES OF EACH SERVICE
	// consulClient, serviceId, err := consul.RegisterConsul(config.ListenPort)
	// if err != nil {
	// 	logger.WithError(err).Error("failed to register consul")
	// 	os.Exit(1)
	// }

	// Start service (including http server)
	go func() {
		service.Start()
	}()

	// Wait for the following shutdown conditions:
	//    1. OS Signal telling us to shutdown
	//    2. The Http Server has shutdown (for whatever reason)
	//    3. The service (application) has shutdown (for whatever reason)
	select {
	case <-osSig:
	case <-service.ShutdownChan():
		// err := consulClient.Deregister(serviceId)
		// if err != nil {
		// 	logger.WithError(err).Warn("Error deregistering service")
		// }
		logger.Info("Service shutdown")
	}

	// Stop service
	// Both the server and the service should have idempotent Shutdown methods, so we call both without
	// worrying what the original cause for shutdown was
	err = service.Stop()
	if err != nil {
		logger.WithError(err)
	}

	return nil
}

func configureLogger(config Config) {
	// Note: We are going to be configuring the standard
	// logger here, so that most packages / libraries can
	// just that use the StandardLogger() as their base
	// are automatically configured. This assumes
	// that app will be the primary configurator of
	// the StandardLogger.
	//
	// Users will be able to override their logger if they
	// provide an option in their constructors, or we can
	// look at explicitly setting loggers instead of mutating
	// the StandardLogger().
	switch strings.ToLower(config.LogType) {
	case "human":
		// Used by default
	case "", "json":
		logrus.SetFormatter(&logrus.JSONFormatter{TimestampFormat: logTimestampFormat})
	default:
		logrus.SetFormatter(&logrus.JSONFormatter{TimestampFormat: logTimestampFormat})
		logrus.StandardLogger().WithField("log_type", config.LogType).Warn("unknown logger type, ignoring")
	}

	// Attempt to auto determine the default level based on environment.
	proeliumEnv, err := env.FromEnvVariable()
	if err == nil {
		switch proeliumEnv {
		case env.ProeliumEnvironmentProd, env.ProeliumEnvironmentMgmt:
			logrus.SetLevel(logrus.InfoLevel)
		default:
			logrus.SetLevel(logrus.DebugLevel)
		}
	}

	// If there are any explicit overrides, use them instead
	level, err := logrus.ParseLevel(strings.ToLower(config.LogLevel))
	if err == nil {
		logrus.SetLevel(level)
	} else {
		logrus.StandardLogger().WithField("log_level", config.LogLevel).Warn("unknown log level, ignoring")
	}

	logrus.SetOutput(os.Stdout)
}
