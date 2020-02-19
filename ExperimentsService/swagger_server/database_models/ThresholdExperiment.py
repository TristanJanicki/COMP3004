from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float, DateTime
import sqlalchemy as db
Base = declarative_base()


class ThresholdExperiment(Base):
    __tablename__ = "ThresholdExperiments"
    experiment_id = Column(Integer, primary_key=True)
    userID = Column(String)
    indicator = Column(String)
    threshold = Column(Float)
    ticker = Column(String)
    status = Column(String) # status can be: update_requested, up_to_date, pending
    update_requested_at = Column(DateTime)

    def __init__(self, userID, indicator, threshold, ticker):
        self.userID = userID
        self.indicator = indicator
        self.threshold = threshold
        self.ticker = ticker

    def __repr__(self):
        return "<ThresholdExperiment(experiment_id='%f', userID='%s', indicator='%s', threshold='%s')>" % (self.experiment_id, self.userID, self.indicator, self.threshold)