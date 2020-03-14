from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime
from sqlalchemy.ext.declarative import declarative_base
import uuid
Base = declarative_base()


class CorrelationExperiment(Base):
    __tablename__ = 'correlation_experiments'
    experiment_id = Column(Integer, primary_key=True)
    asset_1 = Column(String)
    asset_2 = Column(String)
    asset_1_deltas = Column(String)
    asset_2_deltas = Column(String)
    correlation = Column(Float)
    status = Column(String) # status can be: update_requested, up_to_date, pending
    update_requested_at = Column(DateTime)
    last_updated_at = Column(DateTime)

    def __init__(self, experiment_id=uuid.uuid4(), asset_1="", asset_2="", correlation=0, status="updated", update_requested_at="", last_updated_at="", asset_1_deltas=[], asset_2_deltas=[]):
        self.experiment_id = experiment_id
        self.asset_1 = asset_1
        self.asset_2 = asset_2
        self.asset_1_deltas = asset_1_deltas
        self.asset_2_deltas = asset_2_deltas
        self.correlation = correlation
        self.status = status
        self.update_requested_at = update_requested_at

        self.last_updated_at = last_updated_at