from swagger_server.database_models.StatusEnum import StatusEnum
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float, DateTime, Enum
import sqlalchemy as db
Base = declarative_base()


class ThresholdExperiment(Base):
    __tablename__ = "threshold_experiments"
    experiment_id = Column(String, primary_key=True)
    indicator = Column(String)
    threshold = Column(Float)
    ticker = Column(String)
    t_test_t = Column(Float)
    t_test_p = Column(Float)
    shapiro_w2 = Column(Float)
    shapiro_p2 = Column(Float)
    price_deltas = Column(String)
    price_delta_std_dev = Column(Float)
    price_delta_mean = Column(Float)
    volumes = Column(String)
    volumes_mean = Column(Float)
    event_dates = Column(String)
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)
    status = Column(String)
    directional_bias = Column(String)
    price_delta_mode = Column(String)
    skewness = Column(Float)
    kurtosis = Column(Float)

    def __init__(self, experiment_id, indicator, threshold, ticker, status="up_to_date", price_delta_mode=0, update_requested_at=None,  last_updated_at=None, t_test_t=0, t_test_p=0, shapiro_w2=0, shapiro_p2=0, history="", history_std_dev=0, history_mean=0, price_deltas=0, price_delta_std_dev=0, price_delta_mean=0, volumes=0, volumes_mean=0, corr_matrix=0, event_dates="", directional_bias="bearish", skewness=0, kurtosis=0):
        self.experiment_id = experiment_id
        self.indicator = indicator
        self.threshold = threshold
        self.ticker = ticker
        self.status = status
        self.price_deltas = price_deltas
        self.price_delta_mean = price_delta_mean
        self.price_delta_mode = price_delta_mode
        self.price_delta_std_dev = price_delta_std_dev
        self.event_dates = event_dates
        self.t_test_p = t_test_p
        self.t_test_t = t_test_t
        self.shapiro_p2=shapiro_p2
        self.shapiro_w2=shapiro_w2
        self.history=history
        self.history_std_dev=history_std_dev
        self.history_mean=history_mean
        self.price_delta_std_dev=price_delta_mean
        self.volumes=volumes
        self.volumes_mean=volumes_mean,
        self.corr_matrix=corr_matrix
        self.event_dates=""
        self.skewness = skewness
        self.kurtosis = kurtosis
        self.update_requested_at = update_requested_at
        self.directional_bias = directional_bias
        self.last_updated_at = last_updated_at

    def __repr__(self):  # TODO: make proper print statement
        return "<ThresholdExperiment(experiment_id='%s', indicator='%s', threshold='%s')>" % (self.experiment_id, self.indicator, self.threshold)
