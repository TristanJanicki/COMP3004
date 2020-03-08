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
    price_deltas = Column(String)
    price_delta_std_dev = Column(Float)
    price_delta_mean = Column(Float)
    event_dates = Column(String)
    # status = Column(Enum(StatusEnum)) #TODO: figure out how to properly query with this column type and then enforce this enum restriction at the database level. Status can be: update_requested, up_to_date, pending
    status = Column(String)
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)

    def __init__(self, experiment_id, indicator, threshold, ticker, status="up_to_date", update_requested_at=None, price_deltas=None, price_delta_std_dev=None, price_delta_mean=None, event_dates=None, last_updated_at=None):
        self.experiment_id = experiment_id
        self.indicator = indicator
        self.threshold = threshold
        self.ticker = ticker
        self.status = status
        self.price_deltas = price_deltas
        self.price_delta_mean = price_delta_mean
        self.price_delta_std_dev = price_delta_std_dev
        self.event_dates = event_dates
        self.update_requested_at = update_requested_at
        self.last_updated_at = last_updated_at
    def __repr__(self): #TODO: make proper print statement
        return "<ThresholdExperiment(experiment_id='%s', indicator='%s', threshold='%s')>" % (self.experiment_id, self.indicator, self.threshold)