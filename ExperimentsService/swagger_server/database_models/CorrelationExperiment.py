from sqlalchemy import Column, Integer, String, Float, Boolean, DateTime
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class CorrelationExperiment(Base):
    __tablename__ = 'correlation_experiments'
    experiment_id = Column(Integer, primary_key=True)
    user_id = Column(String)
    asset_1 = Column(String)
    asset_2 = Column(String)
    correlation = Column(Float)
    status = Column(String) # status can be: update_requested, up_to_date, pending
    update_requested_at = Column(DateTime)
