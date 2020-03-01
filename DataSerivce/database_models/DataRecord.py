from sqlalchemy import Column, Integer, String, Float, DateTime, Text
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()


class DataRecord(Base):
    __tablename__ = 'data_records'
    id = Column(String, primary_key=True)
    ticker = Column(String)
    dataType = Column(String)
    technical = Column(String)
    seriesType = Column(String)
    timePeriod = Column(String)
    timeInterval = Column(String)
    timeSeriesType = Column(String)
    lastUpdatedAt = Column(DateTime)
    createdAt = Column(DateTime)

    def __init__ (self, id, dataType, technical, seriesType, timePeriod, timeInterval, timeSeriesType):
        self.id = id
        self.dataType = dataType
        self.seriesType = seriesType
        self.timePeriod = timePeriod
        self.timeInterval = timeInterval
        self.timeSeriesType = timeSeriesType

    def __repr__(self):
        return "<DataRecord(id=%s, dataType=%s, seriesType=%s, timePeriod=%s, timeInterval=%s, timeSeriesType=%s)>" %(self.id, self.dataType, self.seriesType, self.timePeriod, self.timeInterval, self.timeSeriesType)