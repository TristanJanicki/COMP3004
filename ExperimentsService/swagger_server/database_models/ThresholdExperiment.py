from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, Float
import sqlalchemy as db
Base = declarative_base()


class ThresholdExperiment(Base):
    __tablename__ = "ThresholdExperiments"
    experiment_id = Column(Integer, primary_key=True)
    userID = Column(String)
    indicator = Column(String)
    threshold = Column(Float)

    def searchByUserID(self, session, userID):
        th_exp = session.query(self).filter_by(userID=userID).first()
        return th_exp
    def __repr__(self):
        return "<ThresholdExperiment(experiment_id='%f', userID='%s', indicator='%s', threshold='%s')>" % (self.experiment_id, self.userID, self.indicator, self.threshold)