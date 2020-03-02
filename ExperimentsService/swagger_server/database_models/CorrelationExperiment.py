from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class CorrelationExperiment(Base):
    __tablename__ = 'correlation_experiments'
    experiment_id = Column(Integer, primary_key=True)
    asset_1 = Column(String)
    asset_2 = Column(String)
    correlation = Column(Float)
    status = Column(String) # status can be: update_requested, up_to_date, pending
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)

    def __init__(self, experiment_id, asset_1, asset_2, correlation, status, update_requested_at, last_updated_at):
        self.experiment_id = experiment_id
        self.asset_1 = asset_1
        self.asset_2 = asset_2
        self.correlation = correlation
        self.status = status
        self.update_requested_at = update_requested_at
        self.last_updated_at = last_updated_at