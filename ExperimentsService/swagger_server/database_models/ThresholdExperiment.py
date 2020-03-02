from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float, DateTime
import sqlalchemy as db
Base = declarative_base()


class ThresholdExperiment(Base):
    __tablename__ = "threshold_experiments"
    experiment_id = Column(String, primary_key=True)
    indicator = Column(String)
    threshold = Column(Float)
    ticker = Column(String)
    status = Column(String) # status can be: update_requested, up_to_date, pending
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)

    def __init__(self, experiment_id, indicator, threshold, ticker, status="up_to_date", update_requested_at=None, last_updated_at=None):
        self.experiment_id = experiment_id
        self.indicator = indicator
        self.threshold = threshold
        self.ticker = ticker
        self.status = status
        self.update_requested_at = update_requested_at
        self.last_updated_at = last_updated_at
    def __repr__(self):
        return "<ThresholdExperiment(experiment_id='%f', userID='%s', indicator='%s', threshold='%s')>" % (self.experiment_id, self.userID, self.indicator, self.threshold)