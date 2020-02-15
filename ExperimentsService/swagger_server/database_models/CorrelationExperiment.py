from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()
from sqlalchemy import Column, Integer, String, Float

class CorrelationExperiment(Base):
    __tablename__ = 'threshold_experiments'
    experiment_id = Column(Integer, primary_key=True)
    userID = Column(String)
    asset_1 = Column(String)
    asset_2 = Column(String)
    correlation = Column(Float)