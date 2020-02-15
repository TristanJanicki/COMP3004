from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()
from sqlalchemy import Column, Integer, String, Float

class ThresholdExperiment(Base):
    __tablename__ = "ThresholdExperiments"
    experiment_id = Column(Integer, primary_key=True)
    userID = Column(String)
    indicator = Column(String)
    threshold = Column(Float)