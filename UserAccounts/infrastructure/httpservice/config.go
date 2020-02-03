package httpservice

import (
	"time"

	"github.com/sirupsen/logrus"
)

type ServiceConfig map[string]interface{}

const (
	SqlDbHost     = "sql_db_host"
	SqlDbPort     = "sql_db_port"
	SqlDbName     = "sql_db_name"
	SqlDbUser     = "sql_db_user"
	SqlDbPassword = "sql_db_password"
)

const (
	DynamoDbDbEndpoint = "dynamo_db_endpoint"
)

type Config struct {
	LogLevel string `mapstructure:"log_level"`
	LogType  string `mapstructure:"log_type"`

	ListenAddress string `mapstructure:"listen_address"`
	ListenPort    int    `mapstructure:"listen_port"`

	ConsulAddress        string        `mapstructure:"consul_address"`
	ConsulPort           int           `mapstructure:"consul_port"`
	ConsulSessionTimeout time.Duration `mapstructure:"zk_session_timeout"`

	StartupGracePeriod        time.Duration `mapstructure:"startup_grace_period"`
	ShutdownGracePeriod       time.Duration `mapstructure:"shutdown_grace_period"`
	DeRegistrationGracePeriod time.Duration `mapstructure:"deregistration_grace_period"`

	// Runtime inspection
	EnablePprof  bool `mapstructure:"enable_pprof"`
	EnableExpvar bool `mapstructure:"enable_expvar"`

	Labels map[string]string `mapstructure:"labels"`

	ServiceConfig ServiceConfig `mapstructure:"service"`

	// TODO add metrics
}

var DefaultConfig = Config{
	LogLevel: "info",
	LogType:  "json",

	ListenAddress: "0.0.0.0",
	ListenPort:    8080,

	ConsulAddress: "0.0.0.0",
	ConsulPort:    8000,

	StartupGracePeriod:  1 * time.Minute,
	ShutdownGracePeriod: 30 * time.Second,

	EnablePprof:  true,
	EnableExpvar: true,

	Labels: make(map[string]string),
}

var DefaultServiceConfig = ServiceConfig{
	SqlDbHost:     "quantr.cii6qa7deotz.us-east-1.rds.amazonaws.com",
	SqlDbPort:     3306,
	SqlDbName:     "quantr",
	SqlDbUser:     "admin",
	SqlDbPassword: "5efPemPEwZrBfhvQ",

	DynamoDbDbEndpoint: "http://localhost:8000",
}

var DefaultLogger = logrus.StandardLogger()
