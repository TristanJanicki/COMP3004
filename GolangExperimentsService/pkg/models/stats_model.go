package models

type StatsModel struct {
	TTestT           float64 `gorm:"type:float;column:t_test_t"`
	TTestP           float64 `gorm:"type:float;column:t_test_p"`
	ShapiroW         float64 `gorm:"type:float;column:shapiro_w"`
	ShapiroP         float64 `gorm:"type:float;column:shapiro_p"`
	PriceDeltas      *string `gorm:"type:nvarchar(50);not null;column:price_deltas"`
	PriceDeltaStdDev float64 `gorm:"type:float;column:price_delta_std_dev"`
	PriceDeltaMean   float64 `gorm:"type:float;column:price_delta_mean"`
	Volumes          *string `gorm:"type:nvarchar(50);not null;column:volumes"`
	VolumesMean      float64 `gorm:"type:float;column:Volumes_mean"`
	PriceDeltaMode   float64 `gorm:"type:float;not null;column:price_delta_mode"`
	Skewness         float64 `gorm:"type:float;column:skewness"`
	Kurtosis         float64 `gorm:"type:float;column:kurtosis"`
}
