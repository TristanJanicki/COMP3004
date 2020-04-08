package models

import (
	"strconv"
	"strings"
	"time"

	genModels "github.com/COMP3004/GolangExperimentsService/pkg/gen/models"
)

type ThresholdExperiment struct {
	ExperimentID     string  `gorm:"primary_key;column:experiment_id"`
	Ticker           *string `gorm:"type:nvarchar(50);not null;column:ticker"`
	Indicator        *string `gorm:"type:nvarchar(50);column:indicator"`
	Threshold        *int64  `gorm:"type:float;column:threshold"`
	EventDates       string  `gorm:"type:nvarchar(50);column:event_dates"`
	Status           *string `gorm:"type:nvarchar(50);column:status"`
	Directional_bias *string `gorm:"type:nvarchar(50);column:directional_bias"`

	StatsModel StatsModel

	CreatedAt *time.Time
	UpdatedAt *time.Time
	DeletedAt *time.Time
}

func ConvertDbThresholdToSwaggerThreshold(exp ThresholdExperiment) *genModels.ExistingThresholdExperiment {
	var convertedPriceDeltas []float64

	for _, val := range strings.Split(*exp.StatsModel.PriceDeltas, ",") {
		i, err := strconv.ParseFloat(val, 64)
		if err != nil {
			i = 0
		}
		convertedPriceDeltas = append(convertedPriceDeltas, i)
	}

	var convertedVolumes []int64

	for _, val := range strings.Split(*exp.StatsModel.Volumes, ",") {
		i, err := strconv.ParseInt(val, 10, 64)
		if err != nil {
			i = 0
		}
		convertedVolumes = append(convertedVolumes, i)
	}

	return &genModels.ExistingThresholdExperiment{
		ExperimentID:     exp.ExperimentID,
		DirectionalBias:  *exp.Directional_bias,
		EventDates:       strings.Split(exp.EventDates, ","),
		Indicator:        *exp.Indicator,
		PriceDeltaMean:   exp.StatsModel.PriceDeltaMean,
		PriceDeltaStdDev: exp.StatsModel.PriceDeltaStdDev,
		PriceDeltas:      convertedPriceDeltas,
		Shapirop:         exp.StatsModel.ShapiroP,
		Shapirow:         exp.StatsModel.ShapiroW,
		Status:           *exp.Status,
		TTestp:           exp.StatsModel.TTestP,
		TTestt:           exp.StatsModel.TTestT,
		Threshold:        *exp.Threshold,
		Ticker:           *exp.Ticker,
		Volumes:          convertedVolumes,
		VolumesMean:      exp.StatsModel.VolumesMean,
	}

}
