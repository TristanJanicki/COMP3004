package mysql

import (
	"fmt"

	_ "github.com/go-sql-driver/mysql"
	"github.com/jinzhu/gorm"
	"github.com/sirupsen/logrus"

	"github.com/COMP3004/UserAccounts/infrastructure/httpservice"
)

type SqlDbManager struct {
	log *logrus.Entry

	Db *gorm.DB

	dbName     string
	dbHost     string
	dbPort     int
	dbUser     string
	dbPassword string
}

func New(config httpservice.ServiceConfig) (*SqlDbManager, error) {
	dbHost := config[httpservice.SqlDbHost].(string)
	dbPort := config[httpservice.SqlDbPort].(int)
	dbName := config[httpservice.SqlDbName].(string)
	dbUser := config[httpservice.SqlDbUser].(string)
	dbPassword := config[httpservice.SqlDbPassword].(string)
	dbUri := fmt.Sprintf("%s:%s@tcp(%s:%d)/%s?parseTime=true", dbUser, dbPassword, dbHost, dbPort, dbName)
	db, err := gorm.Open("mysql", dbUri)
	if err != nil {
		return nil, err
	}
	// Disable pluralizing table names
	db.SingularTable(false)

	return &SqlDbManager{
		log:        logrus.StandardLogger().WithField("type", "db_manager"),
		Db:         db,
		dbName:     dbName,
		dbHost:     dbHost,
		dbPort:     dbPort,
		dbUser:     dbUser,
		dbPassword: dbPassword,
	}, nil
}

func (m SqlDbManager) Close() error {
	m.log.WithFields(logrus.Fields{
		"name": m.dbName,
		"host": m.dbHost,
		"port": m.dbPort,
	}).Info("Stopping database connection")
	err := m.Db.Close()
	if err != nil {
		m.log.WithError(err).Warn("Database connection closed ungracefully")
	} else {
		m.log.Info("Database connection closed gracefully")
	}
	return err
}
