package models

import (
	"math"
	"strconv"
	"strings"
	"time"

	genModels "github.com/COMP3004/GolangExperimentsService/pkg/gen/models"
)

type CorrelationExperiment struct {
	ExperimentID *string `gorm:"primary_key;column:experiment_id"`
	Asset1       *string `gorm:"type:nvarchar(50);not null;column:asset_1"`
	Asset2       *string `gorm:"type:nvarchar(50);not null;column:asset_2"`
	Asset1Deltas string  `gorm:"type:nvarchar(50);not null;column:asset_1_deltas"`
	Asset2Deltas string  `gorm:"type:nvarchar(50);not null;column:asset_2_deltas"`
	Correlation  string  `gorm:"type:float;column:correlation"`
	Status       string  `gorm:"type:nvarchar(50);not null;column:status"`
	AssetCombo   string  `gorm:"type:nvarchar(50);not null;column:asset_combo"` // should only be 'equity_currency','currency_currency'

	CreatedAt *time.Time
	UpdatedAt *time.Time
	DeletedAt *time.Time
}

func ConvertDbCorrelationToSwaggerCorrelation(corr CorrelationExperiment) *genModels.ExistingCorrelationExperiment {

	var convertedAsset1Deltas []float64
	var convertedAsset2Deltas []float32

	splits1 := strings.Split(corr.Asset1Deltas, ",")
	splits2 := strings.Split(corr.Asset2Deltas, ",")

	for index, _ := range len(math.Min(len(splits1), len(splits2))) {
		i, err := strconv.ParseFloat(corr.Asset1Deltas[index], 64)
		if err != nil {
			i = 0
		}
		convertedAsset1Deltas = append(convertedAsset1Deltas, i)

		i, err = strconv.ParseFloat(corr.Asset2Deltas[index], 64)
		if err != nil {
			i = 0
		}
		convertedAsset2Deltas = append(convertedAsset2Deltas, i)
	}

	return &genModels.ExistingCorrelationExperiment{
		ExperimentID: corr.ExperimentID,
		Asset1:       corr.Asset1,
		Asset2:       corr.Asset2,
		Correlation:  corr.Correlation,
		Asset1Deltas: convertedAsset1Deltas,
		Asset2Deltas: convertedAsset2Deltas,
		Type:         &corr.AssetCombo,
	}
}
